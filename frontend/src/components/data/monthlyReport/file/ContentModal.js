import React, { useEffect, useState } from "react";
import Swal from "sweetalert2";

import msg from "../../../common/message";
import CommonModal from "../../../common/CommonModal";
import axios from "axios";
import { getUserRole } from "../../../../CommonFunction";
import { checkUpdateAndDeleteRight } from "../../../../CommonHook";

export default function ContentModal(props) {
    const { mode, data, close, render, allData, retry, sgg, year, collectYn, hasEdit } = props;
    const [files, setFiles] = useState([]);
    const baseUrl = "/api/data/mr/data";

    const insertToUpdate = (formData) => {
        if (formData === undefined) return;

        if (mode === "insert") {
            let submit = new FormData();
            Object.keys(formData).forEach((key) => {
                if (key == "files") {
                    if (formData[key].length == 0) return;

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
        } else if (mode == "update") {
            return axios
                .patch(baseUrl, JSON.stringify({ ...formData, files: null }), {
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

    // 삭제
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
                        console.log(err);
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
                readonly: true,
                placeholder: "구/군_월간보고 표준_yyyyMM",
                col: "12",
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
                id: "year",
                label: "연도",
                type: "yearPicker",
                // readonly: mode === "update" ? true : false,
                col: "6",
            },
            {
                id: "month",
                label: "월",
                type: "select",
                // readonly: mode === "update" ? true : false,
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
                id: "sggCd",
                label: "구군",
                type: "select",
                // readonly: mode === "update" ? true : false,
                option: [
                    ...sgg.map((reg) => ({
                        label: reg.value,
                        value: reg.name,
                    })),
                ],
                col: "6",
            },
            {
                id: "dupInfo",
                label: "중복 행",
                type: "input",
                col: "6",
                placeholder: "공영주차장 기준 중복행 안내",
                readonly: true,
            },
            {
                id: "files",
                label: "파일 업로드",
                type: "files",
                col: "6",
                required: true,
                msg: "1개의 파일을 업로드해야 합니다.",
                disabled: mode === "update" ? true : false,
                uploadName: "월간보고 업로드",
                downloadName: "월간보고",
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
