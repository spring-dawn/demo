import React, { useEffect, useState } from "react";
import Swal from "sweetalert2";

import msg from "../../../../common/message";

import CommonModal from "../../../../common/CommonModal";

import axios from "axios";
import { getUserRole } from "../../../../../CommonFunction";

function ContentModal(props) {
    const { mode, data, close, sgg } = props;
    const btn_search = document.getElementById("btn_search");

    // 등록/수정
    const [formData, setFormData] = useState();
    const insertToUpdate = (formData) => {
        if (formData === undefined) return;
        return Swal.fire(msg.alertMessage["isRegister"]).then((res) => {
            if (!res.isConfirmed) return;
            if (mode == "insert") {
                return axios
                    .post("/api/data/mr/decrease", formData, {
                        withCredentials: true,
                        headers: {
                            "Content-Type": "application/json",
                        },
                    })
                    .then(() => {
                        Swal.fire(msg.alertMessage["insert_success"]);
                        close(false);
                        btn_search.click();
                    });
            } else {
                return axios
                    .patch("/api/data/mr/decrease", formData, {
                        withCredentials: true,
                        headers: {
                            "Content-Type": "application/json",
                        },
                    })
                    .then(() => {
                        Swal.fire(msg.alertMessage["insert_success"]);
                        close(false);
                        btn_search.click();
                    });
            }
            //
        });
    };

    // 삭제
    const [del, setDel] = useState({ id: null, del: false });
    useEffect(() => {
        if (!del.del) return;
        Swal.fire(msg.alertMessage["double_check"]).then((res) => {
            if (res.isConfirmed) {
                axios
                    .delete("/api/data/mr/decrease/" + data.id)
                    .then(() => {
                        Swal.fire(msg.alertMessage["delete"]);
                        close(true);
                        btn_search.click();
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

    // 생성
    const [li, setLi] = useState([]);
    useEffect(() => {
        let liArr = [
            {
                id: "year",
                label: "연도",
                // type: "input",
                // input_type: "text",
                type: "select",
                option: [
                    { label: "2023", value: "2023" },
                    { label: "2022", value: "2022" },
                    { label: "2021", value: "2021" },
                ],
                col: "6",
                // required: true,
            },
            {
                id: "month",
                label: "월",
                type: "select",
                option: [
                    { label: "01", value: "01" },
                    { label: "02", value: "02" },
                    { label: "03", value: "03" },
                    { label: "04", value: "04" },
                    { label: "05", value: "05" },
                    { label: "06", value: "06" },
                    { label: "07", value: "07" },
                    { label: "08", value: "08" },
                    { label: "09", value: "09" },
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
                option: [
                    ...sgg.map((s) => ({
                        value: s.name,
                        label: s.value,
                    })),
                ],
                col: "6",
            },
            {
                id: "reportNo",
                label: "신고번호",
                type: "input",
                input_type: "text",
                col: "6",
                required: false,
            },
            {
                id: "location",
                label: "대지위치",
                type: "input",
                input_type: "text",
                col: "6",
                required: false,
            },
            {
                id: "owner",
                label: "소유자",
                type: "input",
                input_type: "text",
                col: "6",
                required: false,
            },

            {
                id: "buildUsage",
                label: "용도",
                type: "input",
                input_type: "text",
                col: "6",
                required: false,
            },
            {
                id: "demolitionDt",
                label: "철거예정일",
                type: "input",
                input_type: "text",
                col: "6",
                required: false,
            },
            {
                id: "demolitionReason",
                label: "철거사유",
                type: "input",
                input_type: "text",
                col: "6",
                required: false,
            },
            {
                id: "spaces",
                label: "주차대수(면수)",
                type: "input",
                input_type: "number",
                col: "6",
                required: false,
            },
            {
                id: "totalArea",
                label: "총면적(㎡)",
                type: "input",
                input_type: "number",
                step: "0.01",
                col: "6",
                required: false,
            },
            {
                id: "structure",
                label: "구조",
                type: "input",
                input_type: "text",
                col: "6",
                required: false,
            },
        ];

        setLi(liArr);
    }, [sgg]);

    return (
        <CommonModal
            list={li}
            mode={mode}
            data={data}
            close={close}
            form={setFormData}
            setDel={setDel}
            insertToUpdate={insertToUpdate}
            deleteAuth={getUserRole() == "ROLE_ADM"}
            updateAuth={getUserRole() == "ROLE_ADM"}
        />
    );
}

export default ContentModal;
