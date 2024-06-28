import React, { useEffect, useState } from "react";
import Swal from "sweetalert2";

import msg from "../../../../common/message";
import CommonModal from "../../../../common/CommonModal";
import axios from "axios";
import { getUserRole } from "../../../../../CommonFunction";

function ContentModal(props) {
    const { mode, data, close, sgg } = props;
    const btn_search = document.getElementById("btn_search");

    // (등록) 수정
    const [formData, setFormData] = useState();
    const insertToUpdate = (formData) => {
        if (formData === undefined) return;
        if (mode !== "update") return;

        return Swal.fire(msg.alertMessage["isRegister"]).then((res) => {
            if (!res.isConfirmed) return;

            axios
                .patch("/api/data/facility/read/prv", formData, {
                    withCredentials: true,
                    headers: {
                        "Content-Type": "application/json",
                    },
                })
                .then(() => {
                    Swal.fire(msg.alertMessage["insert_success"]);
                    close(false);
                    btn_search.click();
                })
                .catch((err) => alert(err.response.data.message)); // 서버의 예외 메시지를 전달합니다
        });
    };

    // 삭제
    const [del, setDel] = useState({ id: null, del: false });
    useEffect(() => {
        if (!del.del) return;
        Swal.fire(msg.alertMessage["double_check"]).then((res) => {
            if (res.isConfirmed) {
                axios
                    .delete("/api/data/facility/read/prv", {
                        data: data,
                        headers: {
                            "Content-Type": "application/json",
                        },
                    })
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
            // {
            //     id: "mngNo",
            //     label: "일련번호",
            //     type: "input",
            //     input_type: "text",
            //     col: "6",
            //     required: false,
            //     readonly: true,
            // },
            {
                id: "year",
                label: "연도",
                type: "input",
                input_type: "text",
                col: "6",
                required: false,
                readonly: true,
            },
            {
                id: "month",
                label: "월",
                type: "input",
                input_type: "text",
                col: "6",
                required: false,
                readonly: true,
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
                required: true,
                col: "6",
            },
            {
                id: "lotNm",
                label: "주차장명",
                type: "input",
                input_type: "text",
                col: "6",
                required: false,
            },
            {
                id: "address",
                label: "지번주소",
                type: "input",
                input_type: "text",
                col: "6",
                required: false,
            },
            {
                id: "totalSpcs",
                label: "총주차면수",
                type: "input",
                input_type: "text",
                col: "6",
                required: false,
            },
            {
                id: "spcs",
                label: "일반주차면수",
                type: "input",
                input_type: "text",
                col: "6",
                required: false,
            },
            {
                id: "disabledSpcs",
                label: "장애인주차면수",
                type: "input",
                input_type: "text",
                col: "6",
                required: false,
            },
            {
                id: "totalSpcsIn",
                label: "총주차대수",
                type: "input",
                input_type: "text",
                col: "6",
                required: false,
            },
            {
                id: "spcsIn",
                label: "일반주차대수",
                type: "input",
                input_type: "text",
                col: "6",
                required: false,
            },
            {
                id: "disabledSpcsIn",
                label: "장애인주차대수",
                type: "input",
                input_type: "text",
                col: "6",
                required: false,
            },
            {
                id: "ceoCellNo",
                label: "대표번호",
                type: "input",
                input_type: "text",
                col: "6",
                required: false,
            },
            {
                id: "lat",
                label: "위도",
                type: "input",
                input_type: "text",
                col: "6",
                required: false,
            },
            {
                id: "lng",
                label: "경도",
                type: "input",
                input_type: "text",
                col: "6",
                required: false,
            },
            {
                id: "paper",
                label: "급지",
                type: "input",
                input_type: "text",
                col: "6",
                required: false,
            },
            {
                id: "regiType",
                label: "구분",
                type: "input",
                input_type: "text",
                col: "6",
                required: false,
            },
            {
                id: "operateInfo",
                label: "운영정보",
                type: "input",
                input_type: "text",
                col: "12",
                required: false,
            },
            {
                id: "comment",
                label: "비고",
                type: "input",
                input_type: "text",
                col: "12",
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
            insertToUpdate={insertToUpdate}
            form={setFormData}
            setDel={setDel}
            deleteAuth={getUserRole() == "ROLE_ADM"}
            updateAuth={getUserRole() == "ROLE_ADM"}
        />
    );
}

export default ContentModal;
