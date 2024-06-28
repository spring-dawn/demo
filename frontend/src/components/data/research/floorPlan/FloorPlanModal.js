import React, { useEffect, useState } from "react";
import Swal from "sweetalert2";
import msg from "../../../common/message";
import CommonModal from "../../../common/CommonModal";
import axios from "axios";
import { getUserRole } from "../../../../CommonFunction";

function FloorPlanModal(props) {
    const { mode, data, close, render, allData, retry, sgg, year } = props;
    const [files, setFiles] = useState([]);

    const insertToUpdate = (formData) => {
        if (formData !== undefined) {
            if (mode === "insert") {
                let submitFormData = new FormData();

                Object.keys(formData).forEach((key) => {
                    if (key == "files") {
                        [...formData[key]].forEach((file) => {
                            submitFormData.append("files", file); // Append files without an index
                        });
                    } else {
                        submitFormData.append(key, formData[key] || "");
                    }
                });

                return axios
                    .post("/api/data/floorPlan", submitFormData, {
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
                const submitData = {
                    fpNo: formData.fpNo,
                    name: formData.name,
                    year: formData.year,
                    rmrk: formData.rmrk,
                };

                return axios
                    .patch("/api/data/floorPlan", JSON.stringify(submitData), {
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
        }
    };

    // 삭제
    const [del, setDel] = useState({ id: null, del: false });
    useEffect(() => {
        if (del.del) {
            Swal.fire(msg.alertMessage["double_check"]).then((res) => {
                if (res.isConfirmed) {
                    fetch("/api/data/floorPlan?fpNo=" + del.id.fpNo, {
                        method: "DELETE",
                    })
                        .then((res) => {
                            if (res.ok) {
                                Swal.fire(msg.alertMessage["delete"]);
                                close(false);
                                render();
                            } else {
                                return res.json().then((json) => {
                                    throw new Error(json.message);
                                });
                            }
                        })
                        .catch((error) => {
                            Swal.fire({
                                text: error.message,
                                content: {
                                    element: "span",
                                    attributes: {
                                        style: "font-size: 12px;",
                                    },
                                },
                            });
                        });
                } else {
                    // res.dismiss === Swal.DismissReason.cancel;
                    setDel({ id: null, del: false });
                    return;
                }
            });
        }
    }, [del]);

    // 생성
    const [li, setLi] = useState([]);

    useEffect(() => {
        let liArr = [
            {
                id: "name",
                label: "데이터명",
                type: "input",
                input_type: "text",
                col: "12",
                required: true,
                msg: "필수입력 값 입니다.",
            },
            {
                id: "rmrk",
                label: "설명",
                type: "textarea",
                col: "12",
            },
            {
                id: "year",
                label: "연도",
                type: "yearPicker",
                col: "6",
            },
            {
                id: "regCode",
                label: "구군",
                type: "select",
                option:
                    sgg.length > 0
                        ? sgg.map((ele) => ({ value: ele.name, label: ele.value }))
                        : [{ value: "", label: "" }],
                col: "6",
            },
            {
                id: "files",
                label: "파일 업로드",
                type: "files",
                col: "6",
                required: true,
                msg: "최소 하나의 파일을 업로드해야 합니다.",
                disabled: mode === "update" ? true : false,
                uploadName: "도면 업로드",
                downloadName: "보고서",
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
            deleteAuth={getUserRole() == "ROLE_ADM" || getUserRole() == "ROLE_SGG_ADM"}
            updateAuth={getUserRole() == "ROLE_ADM" || getUserRole() == "ROLE_SGG_ADM"}
        />
    );
}

export default FloorPlanModal;
