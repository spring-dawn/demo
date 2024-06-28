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
        // Swal.fire(msg.alertMessage["isRegister"]).then((res) => {
        //     if (!res.isConfirmed) return;
        //     if (mode == "insert") {
        //         axios
        //             .post("/api/data/pf/public", formData, {
        //                 withCredentials: true,
        //                 headers: {
        //                     "Content-Type": "application/json",
        //                 },
        //             })
        //             .then(() => {
        //                 Swal.fire(msg.alertMessage["insert_success"]);
        //                 close(false);
        //                 btn_search.click();
        //             })
        //             .catch((err) => alert(err.response.data.message)); // 서버의 예외 메시지를 전달합니다
        //     } else {
        //         axios
        //             .patch("/api/data/pf/public", formData, {
        //                 withCredentials: true,
        //                 headers: {
        //                     "Content-Type": "application/json",
        //                 },
        //             })
        //             .then(() => {
        //                 Swal.fire(msg.alertMessage["insert_success"]);
        //                 close(false);
        //                 btn_search.click();
        //             })
        //             .catch((err) => alert(err.response.data.message)); // 서버의 예외 메시지를 전달합니다
        //     }
        // });
    };

    // 삭제
    const [del, setDel] = useState({ id: null, del: false });
    useEffect(() => {
        if (!del.del) return;
        Swal.fire(msg.alertMessage["double_check"]).then((res) => {
            if (res.isConfirmed) {
                axios
                    .delete("/api/data/mr/public/" + data.id)
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
                id: "mngNo",
                label: "관리번호",
                type: "input",
                input_type: "text",
                col: "6",
                required: false,
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
                id: "lotType",
                label: "구분유형",
                type: "input",
                input_type: "text",
                col: "6",
                required: false,
            },
            {
                id: "stAddress",
                label: "도로명주소",
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
                label: "주차구획수",
                type: "input",
                input_type: "text",
                col: "6",
                required: false,
            },
            {
                id: "paper",
                label: "급지구분",
                type: "input",
                input_type: "text",
                col: "6",
                required: false,
            },
            {
                id: "year",
                label: "연도",
                type: "input",
                input_type: "text",
                col: "6",
                required: false,
            },
            {
                id: "month",
                label: "월",
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
            insertToUpdate={insertToUpdate}
            form={setFormData}
            setDel={setDel}
            deleteAuth={getUserRole() == "ROLE_ADM"}
            updateAuth={getUserRole() == "ROLE_ADM"}
        />
    );
}

export default ContentModal;
