import React, { useEffect, useState } from "react";
import Swal from "sweetalert2";

import msg from "../../../common/message";
import CommonModal from "../../../common/CommonModal";
import axios from "axios";
import { getUserRole } from "../../../../CommonFunction";
import { checkUpdateAndDeleteRight } from "../../../../CommonHook";

function ReportModal(props) {
    const { mode, data, close, render, allData, retry, sgg, collectYn, hasEdit } = props;
    const [files, setFiles] = useState([]);
    const baseUrl = "/api/data/rsch/data";

    const insertToUpdate = (formData) => {
        if (formData === undefined) return;
        console.log("??", formData);

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
            delete formData.files;
            return axios
                .patch(baseUrl, formData, {
                    headers: {
                        "Content-Type": "application/json",
                    },
                })
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
                    .delete("/api/data/rsch/data/" + data.id)
                    .then(() => {
                        Swal.fire(msg.alertMessage["delete"]);
                        close(true);
                        render();
                    })
                    .catch((err) => {
                        return alert(err.response.data.message);
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
                required: true,
                msg: "필수입력 값 입니다.",
            },
            {
                id: "comment",
                label: "비고",
                type: "textarea",
                col: "12",
            },
            {
                id: "rschType",
                label: "유형",
                type: "select",
                option: [
                    { label: "관리카드", value: "0" },
                    { label: "정리 서식", value: "1" },
                ],
                col: "6",
                disabled: mode === "update" ? true : false,
            },
            {
                id: "year",
                label: "연도",
                type: "yearPicker",
                col: "6",
                disabled: mode === "update" ? true : false,
            },
            {
                id: "sggCd",
                label: "지역",
                type: "select",
                disabled: mode === "update" ? true : false,
                option: [
                    ...sgg.map((reg) => ({
                        label: reg.value,
                        value: reg.name,
                    })),
                ],
                col: "6",
            },
            {
                id: "files",
                label: "파일 업로드",
                type: "files",
                col: "6",
                required: false,
                msg: "하나의 파일을 업로드해야 합니다.",
                disabled: mode === "update" ? true : false,
                uploadName: "조사자료 업로드",
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
            deleteAuth={checkUpdateAndDeleteRight(hasEdit) && collectYn == "N"}
            updateAuth={checkUpdateAndDeleteRight(hasEdit) && collectYn == "N"}
        />
    );
}

export default ReportModal;
