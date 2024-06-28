import React, { useEffect, useState, useCallback } from "react";
import { useForm } from "react-hook-form";
import Swal from "sweetalert2";

import msg from "../../common/message";

import CommonModal from "../../common/CommonModal";

function ContentModal(props) {
    const { mode, data, close } = props;

    // 저장
    const [formData, setFormData] = useState();
    useEffect(() => {
        if (formData !== undefined) {
            fetch("/api/system/role/roles", {
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
        }
    }, [formData]);

    // 삭제
    const [del, setDel] = useState({ id: null, del: false });
    useEffect(() => {
        if (del.del) {
            Swal.fire(msg.alertMessage["double_check"]).then((res) => {
                if (res.isConfirmed) {
                    fetch("/api/system/role/roles/" + del.id.id, {
                        method: "DELETE",
                    }).then((res) => {
                        if (res.status === 200) {
                            Swal.fire(msg.alertMessage["delete"]);
                            close(true);
                            // ag-grid 갱신
                            document.querySelector(".btn_search").click();
                        } else {
                            Swal.fire(msg.alertMessage["delete_fail"]);
                            return;
                        }
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
    const [dtl, setDtl] = useState(null);
    useEffect(() => {
        let liArr = [
            {
                id: "name",
                label: "코드",
                type: "input",
                input_type: "text",
                col: "6",
                required: true,
                msg: "필수 입력 값 입니다.",
                readonly: mode === "update" ? true : false,
            },
            {
                id: "label",
                label: "코드명",
                type: "input",
                input_type: "text",
                col: "6",
                required: true,
                msg: "필수 입력 값 입니다.",
            },
            {
                id: "comment",
                label: "비고",
                type: "textarea",
                col: "12",
            },
        ];

        setLi(liArr);

        // detail
        let dtlArr = [
            { id: "1", name: "메인", role: "Y" },
            { id: "2", name: "시스템관리", role: "" },
            { id: "3", name: "사용자관리", role: "" },
            { id: "4", name: "권한관리", role: "" },
            { id: "5", name: "코드관리", role: "" },
            { id: "6", name: "데이터관리", role: "" },
            { id: "7", name: "실태조사관리", role: "" },
            { id: "8", name: "데이터관리", role: "" },
            { id: "9", name: "연계관리", role: "" },
            { id: "10", name: "GIS시각화", role: "" },
            { id: "11", name: "공간분석/통계", role: "" },
        ];

        let tmpObj = {};
        tmpObj["data"] = dtlArr;

        tmpObj["columnDefs"] = [
            { field: "id", headerName: "id", filter: false },
            { field: "name", headerName: "메뉴명", filter: false },
            {
                field: "READ_PRIVILEGE",
                headerName: "읽기",
                filter: false,
                // colSpan: (params) => {
                //     if (mode === "update") {
                //         if (params.data.role === "Y") {
                //             return <input type="checkbox" checked />;
                //         } else {
                //             return <input type="checkbox" />;
                //         }
                //     }
                // },
                cellRenderer: (params) => {
                    return (
                        <div>
                            {mode === "update" && params.value === "Y" ? (
                                <input type="checkbox" checked />
                            ) : (
                                <input type="checkbox" />
                            )}
                        </div>
                    );
                },
            },
            {
                field: "WRITE_PRIVILEGE",
                headerName: "쓰기",
                filter: false,
                cellRenderer: (params) => {
                    return (
                        <div>
                            {mode === "update" && params.value === "Y" ? (
                                <input type="checkbox" checked />
                            ) : (
                                <input type="checkbox" />
                            )}
                        </div>
                    );
                },
            },
        ];

        setDtl(tmpObj);
    }, []);

    return <CommonModal list={li} dtl={dtl} mode={mode} data={data} close={close} form={setFormData} setDel={setDel} />;
}

export default ContentModal;
