import React, { useEffect, useState } from "react";
import { useForm } from "react-hook-form";
import Swal from "sweetalert2";

import msg from "../../common/message";

function ContentModal(props) {
    const { mode, data, close } = props;
    const {
        handleSubmit,
        register,
        formState: { errors },
        reset,
        getValues,
        setValue,
    } = useForm();

    const onSubmit = (data) => {
        fetch("/api/system/code/codes", {
            method: "post",
            credentials: "include",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify(data),
        }).then((res) => {
            if (res.status === 200) {
                Swal.fire(msg.alertMessage["insert_success"]);
                close(false);
            }
        });
    };

    useEffect(() => {
        if (data) {
            reset(data);
        }
    }, [data, reset]);

    const [readOnly, setReadOnly] = useState(false);
    if (mode === "update") {
        useEffect(() => {
            setReadOnly(true);
        }, [mode]);
    }

    return (
        <form onSubmit={handleSubmit(onSubmit)}>
            <div className="modal_body">
                <ul className="_row">
                    <li className="_col_gap _col6">
                        <div className="form_group">
                            <label htmlFor="name">
                                코드명
                                <span className="required" />
                            </label>
                            <div className="input_group">
                                <input
                                    type="text"
                                    id="name"
                                    className="form_control"
                                    {...register("name", {
                                        required: "필수 입력 값 입니다.",
                                    })}
                                    readOnly={readOnly}
                                />
                                <span className={errors.name !== undefined ? "help_block" : ""}>
                                    {errors.name?.message}
                                </span>
                            </div>
                        </div>
                    </li>

                    <li className="_col_gap _col6">
                        <div className="form_group">
                            <label htmlFor="value">코드값</label>
                            <div className="input_group">
                                <input type="text" id="value" className="form_control" {...register("value")} />
                                <span className={errors.value !== undefined ? "help_block" : ""}>
                                    {errors.value?.message}
                                </span>
                            </div>
                        </div>
                    </li>
                    {/* <li className="_col_gap _col6">
                        <div className="form_group">
                            <label htmlFor="useYn">사용여부</label>
                            <div className="input_group">
                                <select
                                    id="useYn"
                                    className="form_control"
                                    defaultValue="Y"
                                    {...register("useYn", { required: "필수 입력 값 입니다." })}
                                >
                                    <option value="Y">사용</option>
                                    <option value="N">미사용</option>
                                </select>
                            </div>
                        </div>
                    </li> */}
                    <li className="_col_gap _col12">
                        <div className="form_group">
                            <label htmlFor="comment">비고</label>
                            <div className="input_group">
                                <textarea
                                    id="comment"
                                    rows="3"
                                    className="form_control"
                                    {...register("comment")}
                                ></textarea>
                            </div>
                        </div>
                    </li>
                </ul>
            </div>
            <footer>
                <div className="btnWrap">
                    <button className="btn btn_close" onClick={close}>
                        닫기
                    </button>
                    <button className="btn btn_save" type="submit">
                        {mode === "update" ? "수정" : "등록"}
                    </button>
                </div>
            </footer>
        </form>
    );
}

export default ContentModal;
