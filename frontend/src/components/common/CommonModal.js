import React, { useEffect, useState } from "react";
import { useLocation } from "react-router-dom";
import { useForm, Controller } from "react-hook-form";
import { SketchPicker, CirclePicker } from "react-color";
import { UploadOutlined } from "@ant-design/icons";
import { Button, Upload, message } from "antd";
import Swal from "sweetalert2";
import JSZip from "jszip";
import DatePicker from "react-datepicker";
import moment from "moment";
import CommonModalDtl from "./CommonModalDtl";

function CommonModal(props) {
    const location = useLocation();
    const [isSubmit, setIsSubmit] = useState(false);
    const [deleteAuth, setDeleteAuth] = useState(props.deleteAuth);
    const [updateAuth, setUpdateAuth] = useState(props.updateAuth);

    const { list, dtl, mode, retry, data, close, setDel, chkFn, insertToUpdate, deleteDisabled } = props;

    // let deleteOn = mode === "update" && !deleteDisabled;

    // PfSub Id Setting
    const targetId = {
        user: "userId",
        code: "id",
    };

    const {
        handleSubmit,
        register,
        formState: { errors },
        reset,
        getValues,
        setValue,
        setError,
        watch,
        clearErrors,
        control,
    } = useForm();

    // 기본 값이 있을 경우 셋팅
    useEffect(() => {
        reset(data);
    }, []);

    // list 요소가 바뀌면 useForm 데이터 변경
    useEffect(() => {
        const hookValues = getValues();
        const resetData = {};

        // select 요소가 바뀌어서 서브 셀렉트가 생성될 경우 변경
        list.filter((li) => li.type === "select" && li.option.length > 0).forEach((li) => {
            const hookValue = hookValues[li.id];
            const selectedOption = li.option.find((o) => o.value === hookValue);

            const valueToSet = selectedOption ? selectedOption.value : li.option[0].value;
            resetData[li.id] = valueToSet;
        });

        reset({ ...hookValues, ...resetData });
    }, [list]);

    // Input Value Change
    const [change, setChange] = useState();
    useEffect(() => {
        if (change !== undefined && change.target.id === "userId") {
            chkFn(false);
        }
    }, [change]);

    // 외부 insertToUpdate 함수 핸들러 (등록, 수정)
    const onSubmit = (data) => {
        // 파일은 따로 관리
        const errorFilesLi = list.filter(
            (ele) => ele.type == "files" && ele.required && !ele.files.length && !ele.disabled
        );

        if (errorFilesLi.length) {
            errorFilesLi.forEach((ele) => {
                setError(ele.id, {
                    type: "manual",
                    message: ele.msg,
                });
            });
        } else {
            setIsSubmit(true);
            insertToUpdate(data).catch((error) => {
                console.log(error);

                Swal.fire({
                    text: error.response.data.message,
                    content: {
                        element: "span",
                        attributes: {
                            style: "font-size: 12px;",
                        },
                    },
                });

                setIsSubmit(false);
            });
        }
    };

    // Delete
    const target = location.pathname.split("/")[location.pathname.split("/").length - 1];
    const btnDelete = (e) => {
        e.preventDefault();

        let id = getValues(targetId[target]);
        setDel({ id: id, del: true });
    };

    return (
        <form onSubmit={handleSubmit(onSubmit)}>
            <div className="modal_body">
                <ul className="_row">
                    {list.length !== 0 &&
                        list.map((li) => {
                            return (
                                <li key={li.id} className={li.col ? `_col_gap _col${li.col}` : null}>
                                    <div className="form_group">
                                        {li.type !== "button" && li.type !== "comment" ? (
                                            <label htmlFor={li.id}>
                                                {li.label}
                                                {li.required ? <span>(*)</span> : null}
                                            </label>
                                        ) : null}
                                        <div
                                            className="input_group"
                                            style={li.type == "comment" ? { width: "100%", padding: "7px 0" } : {}}
                                        >
                                            {li.type === "input" ? (
                                                <>
                                                    <input
                                                        type={li.input_type}
                                                        step={li.step != undefined && li.step ? li.step : null}
                                                        id={li.id}
                                                        className="form_control"
                                                        {...register(li.id, {
                                                            required: li.msg,
                                                            onChange: (e) => setChange(e),
                                                        })}
                                                        readOnly={
                                                            li.readonly !== undefined && li.readonly ? "readonly" : null
                                                        }
                                                        placeholder={li.placeholder || ""}
                                                        defaultValue={li.defaultValue || ""}
                                                    />
                                                    <span className={errors[li.id] !== undefined ? "help_block" : ""}>
                                                        {errors[li.id]?.message}
                                                    </span>
                                                </>
                                            ) : li.type === "select" ? (
                                                <select
                                                    id={li.id}
                                                    htmlFor={li.id}
                                                    className="form_control"
                                                    disabled={li.disabled}
                                                    {...register(li.id, {
                                                        required: li.required ? "필수 입력 값 입니다." : false,
                                                        onChange: li.change
                                                            ? ({ target }) => li.change(target.value)
                                                            : undefined,
                                                    })}
                                                >
                                                    {li.option.map((m) => {
                                                        return (
                                                            <option key={m.label} value={m.value}>
                                                                {m.label}
                                                            </option>
                                                        );
                                                    })}
                                                </select>
                                            ) : li.type === "textarea" ? (
                                                <textarea
                                                    id={li.id}
                                                    rows="3"
                                                    className="form_control"
                                                    readOnly={
                                                        li.readonly !== undefined && li.readonly ? "readonly" : null
                                                    }
                                                    {...register(li.id)}
                                                ></textarea>
                                            ) : li.type === "button" ? (
                                                <button className={li.class} onClick={li.evt}>
                                                    {li.label}
                                                </button>
                                            ) : li.type === "files" ? (
                                                mode == "update" ? (
                                                    <>
                                                        <div
                                                            className="file_download"
                                                            onClick={() => {
                                                                data[li.id].map((file) => {
                                                                    fetch(`/api/file/download/${file.id} `)
                                                                        .then((response) => response.blob())
                                                                        .then((blob) => {
                                                                            const url =
                                                                                window.URL.createObjectURL(blob);
                                                                            const a = document.createElement("a");
                                                                            a.href = url;
                                                                            a.download = file.fileNm; // Specify the file name
                                                                            document.body.appendChild(a);
                                                                            a.click();
                                                                            window.URL.revokeObjectURL(url);
                                                                            document.body.removeChild(a);
                                                                        });
                                                                });

                                                                // const zip = new JSZip();

                                                                // const promises = data[li.id].map((file) => {
                                                                //     return fetch(`/api/file/${file.id}`) // API 엔드포인트를 적절히 변경해야 합니다.
                                                                //         .then((response) => response.blob())
                                                                //         .then((blob) => {
                                                                //             zip.file(file.fileNm, blob);
                                                                //         });
                                                                // });
                                                                //
                                                                // // 모든 Promise가 해결될 때 Zip 파일 생성 및 다운로드
                                                                // Promise.all(promises).then(() => {
                                                                //     zip.generateAsync({ type: "blob" }).then(
                                                                //         (content) => {
                                                                //             const url =
                                                                //                 window.URL.createObjectURL(content);
                                                                //             const a = document.createElement("a");
                                                                //             a.href = url;
                                                                //             a.download =
                                                                //                 li.downloadName || "다운로드파일.zip"; // Zip 파일 이름 지정
                                                                //             a.click();
                                                                //             window.URL.revokeObjectURL(url);
                                                                //         }
                                                                //     );
                                                                // });
                                                            }}
                                                        >
                                                            전체 다운로드
                                                        </div>
                                                        <ul className={"file_list"}>
                                                            {data[li.id]?.map((file) => {
                                                                return <li key={file.fileNm}>{file.fileNm}</li>;
                                                            })}
                                                        </ul>
                                                    </>
                                                ) : (
                                                    <>
                                                        <Upload
                                                            multiple={true}
                                                            beforeUpload={(file, fileList) => {
                                                                return false;
                                                            }}
                                                            onChange={({ fileList }) => {
                                                                const files = fileList
                                                                    .filter((file) => !file.error)
                                                                    .map((ele) => ele.originFileObj);
                                                                li.setFiles(files);
                                                                setValue(li.id, files);
                                                            }}
                                                        >
                                                            <Button icon={<UploadOutlined />}>{li.uploadName}</Button>
                                                        </Upload>
                                                        <span
                                                            className={errors[li.id] !== undefined ? "help_block" : ""}
                                                        >
                                                            {errors[li.id]?.message}
                                                        </span>
                                                    </>
                                                )
                                            ) : li.type === "color" ? (
                                                <>
                                                    <input
                                                        style={{ marginBottom: "12px", border: "1px solid #e3e3e3" }}
                                                        type={"text"}
                                                        disabled={true}
                                                        value={`rgba(${li.color.rgb.r},${li.color.rgb.g},${li.color.rgb.b},${li.color.rgb.a})`}
                                                    />
                                                    {li.load && li.load}
                                                    <SketchPicker
                                                        width={"100%"}
                                                        color={li.color.rgb}
                                                        onChangeComplete={(color) => {
                                                            const formatColor = li.format(color);
                                                            li.setColor(formatColor);
                                                        }}
                                                    />
                                                </>
                                            ) : li.type === "comment" ? (
                                                <>
                                                    <div style={{ fontSize: "0.9em", color: "#aca7a7" }}>{li.msg}</div>
                                                </>
                                            ) : li.type === "yearPicker" ? (
                                                <>
                                                    <Controller
                                                        name={li.id}
                                                        control={control}
                                                        defaultValue={
                                                            watch(li.id) ? watch(li.id) : moment().format("yyyy")
                                                        }
                                                        render={({ field }) => (
                                                            <DatePicker
                                                                {...field}
                                                                selected={
                                                                    field.value ? new Date(field.value) : new Date()
                                                                }
                                                                onChange={(date) => {
                                                                    setValue(li.id, moment(date).format("yyyy"));
                                                                }}
                                                                showYearPicker
                                                                dateFormat="yyyy"
                                                                disabled={li.disabled}
                                                            />
                                                        )}
                                                    />
                                                </>
                                            ) : null}
                                        </div>
                                    </div>
                                </li>
                            );
                        })}
                </ul>

                {dtl !== undefined && dtl !== null ? <CommonModalDtl data={dtl} /> : null}
            </div>

            <footer>
                <div className="btnWrap">
                    {mode === "update" && !deleteDisabled && deleteAuth ? (
                        <button className="btn btn_delete" onClick={btnDelete}>
                            삭제
                        </button>
                    ) : null}
                    <button className="btn btn_close" onClick={close}>
                        닫기
                    </button>
                    {mode === "insert" || updateAuth ? (
                        <button
                            className="btn btn_save"
                            type="submit"
                            style={{ pointerEvents: `${isSubmit ? "none" : "auto"}` }}
                        >
                            {mode === "update" ? "수정" : retry ? "재등록" : "등록"}
                        </button>
                    ) : null}
                </div>
            </footer>
        </form>
    );
}

export default CommonModal;
