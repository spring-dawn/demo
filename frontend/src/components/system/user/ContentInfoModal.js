import React, { useEffect, useState } from "react";
import Swal from "sweetalert2";

import msg from "../../common/message";

import CommonModal from "../../common/CommonModal";
import axios from "axios";
import { getUserRole } from "../../../CommonFunction";

function ContentInfoModal(props) {
    const { mode, data, close, render, roles, sgg } = props;
    const btn_search = document.getElementById("btn_search");

    // 등록 기능은 사용자가 원치 않아 트리거 제거, 수정 기능만 활성화.
    const insertToUpdate = (formData) => {
        if (formData === undefined) return;

        return Swal.fire(msg.alertMessage["isUpdate"]).then((res) => {
            if (!res.isConfirmed) return;

            return axios
                .patch("/api/system/user/users/adm", formData, {
                    withCredentials: true,
                    headers: {
                        "Content-Type": "application/json",
                    },
                })
                .then(() => {
                    Swal.fire(msg.alertMessage["insert_success"]);
                    close(false);
                    document.querySelector("button").click();
                });
        });
    };

    // 삭제
    const [del, setDel] = useState({ id: null, del: false });
    useEffect(() => {
        if (!del.del) return;
        Swal.fire(msg.alertMessage["double_check"]).then((res) => {
            if (res.isConfirmed) {
                axios
                    .delete("/api/system/user/users/" + del.id)
                    .then(() => {
                        Swal.fire(msg.alertMessage["delete"]);
                        close(true);
                        document.querySelector("button").click();
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
                id: "userId",
                label: "아이디",
                type: "input",
                input_type: "text",
                col: "6",
                required: true,
                msg: "아이디를 입력해주세요",
                readonly: mode === "update" ? true : false,
            },
            {
                id: "userNm",
                label: "사용자명",
                type: "input",
                input_type: "text",
                col: "6",
                required: false,
            },
            {
                id: "email",
                label: "이메일",
                type: "input",
                input_type: "text",
                col: "6",
                required: true,
                msg: "필수입력 값 입니다.",
            },
            {
                id: "cellNo",
                label: "연락처",
                type: "input",
                input_type: "text",
                placeholder: "000-0000-0000",
                col: "6",
                required: false,
            },
            {
                id: "agency",
                label: "소속",
                type: "select",
                option: [
                    { value: "31000", label: "본청" },
                    ...sgg.map((s) => ({
                        value: s.name,
                        label: s.value,
                    })),
                ],
                col: "6",
                required: false,
            },
            {
                id: "dept",
                label: "부서",
                type: "input",
                input_type: "text",
                col: "6",
                required: false,
            },
            {
                id: "roleNm",
                label: "권한",
                type: "select",
                option: [
                    ...roles.map((role) => ({
                        value: role.name,
                        label: role.encodedNm,
                    })),
                ],
                col: "6",
            },
            {
                id: "useYn",
                label: "상태",
                type: "select",
                option: [
                    { value: "Y", label: "사용" },
                    { value: "N", label: "미사용" },
                ],
                col: "6",
            },

            // 관리자의 사용자 등록 기능 제거 => 비고란 사용X.
            {
                id: "roleReqMsg",
                label: "권한 요청",
                type: "input",
                readonly: true,
                col: "12",
            },
        ];

        setLi(liArr);
    }, []);

    return (
        // <CommonModal list={li} mode={mode} data={data} close={close} form={setFormData} setDel={setDel} chkFn={onChk} />
        <CommonModal
            list={li}
            mode={mode}
            data={data}
            close={close}
            setDel={setDel}
            insertToUpdate={insertToUpdate}
            deleteAuth={getUserRole() == "ROLE_ADM"}
            updateAuth={getUserRole() == "ROLE_ADM"}
        />
    );
}

export default ContentInfoModal;
