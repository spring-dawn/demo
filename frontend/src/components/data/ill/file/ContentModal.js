import React, { useEffect, useState } from "react";
import Swal from "sweetalert2";

import msg from "../../../common/message";
import CommonModal from "../../../common/CommonModal";
import axios from "axios";
import { checkUpdateAndDeleteRight } from "../../../../CommonHook";

export default function ContentModal(props) {
    const { mode, data, close, render, allData, retry, sgg, collectYn, hasEdit } = props;
    const [files, setFiles] = useState([]);
    const baseUrl = "/api/data/illegal/data";

    const insertToUpdate = (formData) => {
        if (formData === undefined) return;

        if (mode === "insert") {
            let submit = new FormData();
            Object.keys(formData).forEach((key) => {
                if (key == "files") {
                    [...formData[key]].forEach((file) => {
                        submit.append("files", file); // Append files without an index
                    });
                } else {
                    submit.append(key, formData[key] || "");
                }
            });

            return axios
                .post(baseUrl, submit, {
                    headers: {
                        "Content-Type": "multipart/form-data",
                    },
                })
                .then(() => {
                    Swal.fire(msg.alertMessage["insert_success"]);
                    close(false);
                    render();
                });
        } else {
            // update. 첨부파일은 바꿀 수 없고 삭제만 가능.
            return axios
                .patch(
                    baseUrl,
                    { ...formData, files: null },
                    {
                        headers: {
                            "Content-Type": "application/json",
                        },
                    }
                )
                .then(() => {
                    Swal.fire(msg.alertMessage["insert_success"]);
                    close(false);
                    render();
                });
        }
    };

    // 삭제 todo: 왜 data??
    const [del, setDel] = useState({ id: null, del: false });
    useEffect(() => {
        if (!del.del) return;

        Swal.fire(msg.alertMessage["double_check"]).then((res) => {
            if (res.isConfirmed) {
                axios
                    .delete(baseUrl + "/" + data.id)
                    .then(() => {
                        Swal.fire(msg.alertMessage["delete"]);
                        close(true);
                        render();
                    })
                    .catch((err) => {
                        return err.response.data.message;
                    });
            } else {
                setDel({ id: null, del: false });
                return;
            }
        });
    }, [del]);

    // 모달 input 생성
    const [li, setLi] = useState([]);
    useEffect(() => {
        let liArr = [
            {
                id: "dataNm",
                label: "데이터명",
                type: "input",
                input_type: "text",
                col: "12",
                readonly: true,
                placeholder: "구/군_불법주정차 단속실적_yyyyMM",
                // required: true,
                // msg: "필수입력 값 입니다.",
            },
            {
                id: "comment",
                label: "비고",
                type: "textarea",
                col: "12",
            },
            {
                id: "sggCd",
                label: "구군",
                type: "select",
                // disabled: mode === "update" ? true : false,
                option: [
                    ...sgg.map((reg) => ({
                        label: reg.value,
                        value: reg.name,
                    })),
                ],
                col: "6",
            },
            {
                id: "dataType",
                label: "단속유형",
                type: "select",
                option: [
                    { label: "단속실적", value: "1" },
                    // { label: "적발대장", value: "2" },
                    { label: "그 외 미구현", value: "3" },
                ],
                col: "6",
            },
            {
                id: "year",
                label: "연도",
                type: "yearPicker",
                col: "6",
            },
            {
                id: "month",
                label: "월",
                type: "select",
                option: [
                    { label: "1", value: "01" },
                    { label: "2", value: "02" },
                    { label: "3", value: "03" },
                    { label: "4", value: "04" },
                    { label: "5", value: "05" },
                    { label: "6", value: "06" },
                    { label: "7", value: "07" },
                    { label: "8", value: "08" },
                    { label: "9", value: "09" },
                    { label: "10", value: "10" },
                    { label: "11", value: "11" },
                    { label: "12", value: "12" },
                ],
                col: "6",
            },
            {
                id: "files",
                label: "파일 업로드",
                type: "files",
                col: "6",
                required: true,
                msg: "1개의 파일을 업로드해야 합니다.",
                disabled: mode === "update" ? true : false,
                uploadName: "불법주정차 업로드",
                downloadName: "불법주정차",
                files: files,
                setFiles: (files) => setFiles(Array.from(files)),
            },
        ];
        setLi(liArr);
    }, [files]);
    return (
        <CommonModal
            list={li}
            mode={mode}
            data={data}
            close={close}
            insertToUpdate={insertToUpdate}
            setDel={setDel}
            deleteAuth={collectYn != "Y" && checkUpdateAndDeleteRight(hasEdit)}
            updateAuth={collectYn == "N" && checkUpdateAndDeleteRight(hasEdit)}
        />
    );
}
