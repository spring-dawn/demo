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

        Swal.fire(msg.alertMessage["isRegister"]).then(
            (res) => {
                if (!res.isConfirmed) return;
                console.log("req", formData); // req 값에 id 가 없음
                return alert("미구현입니다.");
                // axios
                //     .patch("/api/data/mr/public", formData, {
                //         withCredentials: true,
                //         headers: {
                //             "Content-Type": "application/json",
                //         },
                //     })
                //     .then(() => {
                //         Swal.fire(msg.alertMessage["insert_success"]);
                //         close(false);
                //         btn_search.click();
                //     })
                //     .catch((err) => alert(err.response.data.message)); // 서버의 예외 메시지를 전달합니다
            }
            //
        );
    };

    // 삭제
    const [del, setDel] = useState({ id: null, del: false });
    useEffect(() => {
        if (!del.del) return;
        // Swal.fire(msg.alertMessage["double_check"]).then((res) => {
        //     if (res.isConfirmed) {
        //         axios
        //             .delete("/api/data/mr/public/" + data.id)
        //             .then(() => {
        //                 Swal.fire(msg.alertMessage["delete"]);
        //                 close(true);
        //                 btn_search.click();
        //             })
        //             .catch((err) => {
        //                 return alert(err.response.data.message);
        //             });
        //     } else {
        //         setDel({ id: null, del: false });
        //         return;
        //     }
        // });
    }, [del]);

    // 생성
    const [li, setLi] = useState([]);
    useEffect(() => {
        let liArr = [
            {
                id: "year",
                label: "연도",
                type: "select",
                option: [
                    { label: "2023", value: "2023" },
                    { label: "2022", value: "2022" },
                    { label: "2021", value: "2021" },
                ],
                col: "6",
                required: true,
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
                required: true,
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
                required: true,
                col: "6",
            },
            {
                id: "name",
                label: "주차장명",
                type: "input",
                input_type: "text",
                col: "6",
                required: false,
            },
            {
                id: "installDt",
                label: "설치일자",
                type: "input",
                input_type: "text",
                col: "6",
                required: false,
            },
            {
                id: "location",
                label: "위치",
                type: "input",
                input_type: "text",
                col: "6",
                required: false,
            },
            {
                id: "wh",
                label: "평일",
                type: "input",
                input_type: "text",
                placeholder: "운영시간",
                col: "6",
                required: false,
            },
            {
                id: "whSaturday",
                label: "토요일",
                type: "input",
                input_type: "text",
                placeholder: "운영시간",
                col: "6",
                required: false,
            },
            {
                id: "whHoliday",
                label: "공휴일",
                type: "input",
                input_type: "text",
                placeholder: "운영시간",
                col: "6",
                required: false,
            },
            {
                id: "dayOff",
                label: "휴무일",
                type: "input",
                input_type: "text",
                col: "6",
                required: false,
            },
            {
                id: "payYn",
                label: "유/무료",
                type: "select",
                option: [
                    { label: "유료", value: "Y" },
                    { label: "무료", value: "N" },
                ],
                col: "6",
                required: false,
            },
            {
                id: "pay4Hour",
                label: "1시간요금",
                type: "input",
                input_type: "text",
                col: "6",
                required: false,
            },
            {
                id: "pay4Day",
                label: "1일요금",
                type: "input",
                input_type: "text",
                col: "6",
                required: false,
            },
            {
                id: "totalSpaces",
                label: "전체주차면수",
                type: "input",
                input_type: "number",
                col: "6",
                placeholder: "일반주차면수 + 전용구획",
                readonly: true,
                required: false,
            },
            {
                id: "spaces",
                label: "일반주차면수",
                type: "input",
                input_type: "number",
                col: "6",
                required: false,
            },
            {
                id: "forDisabled",
                label: "장애인전용",
                type: "input",
                input_type: "number",
                col: "6",
                required: false,
            },
            {
                id: "forLight",
                label: "경차전용",
                type: "input",
                input_type: "number",
                col: "6",
                required: false,
            },
            {
                id: "forPregnant",
                label: "임산부전용",
                type: "input",
                input_type: "number",
                col: "6",
                required: false,
            },
            {
                id: "forBus",
                label: "버스전용",
                type: "input",
                input_type: "number",
                col: "6",
                required: false,
            },
            {
                id: "forElectric",
                label: "전기차전용",
                type: "input",
                input_type: "number",
                col: "6",
                required: false,
            },
            {
                id: "roadYn",
                label: "노상/노외",
                type: "select",
                option: [
                    { label: "노상", value: "Y" },
                    { label: "노외", value: "N" },
                ],
                col: "6",
                required: false,
            },
            {
                id: "owner",
                label: "소유",
                type: "input",
                input_type: "text",
                col: "6",
                required: false,
            },
            {
                id: "agency",
                label: "운영기관",
                type: "input",
                input_type: "text",
                col: "6",
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
