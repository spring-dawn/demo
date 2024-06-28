import React, { useEffect, useState } from "react";
import Swal from "sweetalert2";

import msg from "../../common/message";

import CommonModal from "../../common/CommonModal";
import axios from "axios";
import { getUserRole } from "../../../CommonFunction";

function ContentPasswordModal(props) {
    const { mode, data, close, render, roles, sgg } = props;

    // 등록 기능은 사용자가 원치 않아 트리거 제거, 수정 기능만 활성화.
    const insertToUpdate = (formData) => {
        if (formData === undefined) return;

        return Swal.fire(msg.alertMessage["isUpdate"]).then((res) => {
            if (!res.isConfirmed) return;

            return axios
                .patch("/api/system/user/users/dtl", formData, {
                    withCredentials: true,
                    headers: {
                        "Content-Type": "application/json",
                    },
                })
                .then((response) => {
                    Swal.fire(msg.alertMessage["insert_success"]);
                    localStorage.setItem("user", JSON.stringify(response.data));
                    close(false);
                    document.querySelector("button").click();
                });
        });
    };

    // 생성
    const [li, setLi] = useState([]);
    useEffect(() => {
        let liArr = [
            {
                id: "password",
                label: "비밀번호",
                type: "input",
                input_type: "text",
                col: "12",
                required: true,
                msg: "필수입력 값 입니다",
            },
            {
                id: "passwordNew",
                label: "새 비밀번호",
                type: "input",
                input_type: "text",
                col: "12",
                required: true,
                msg: "필수입력 값 입니다",
            },
            {
                id: "passwordNewConfirm",
                label: "비밀번호 확인",
                type: "input",
                input_type: "text",
                col: "12",
                required: true,
                msg: "필수입력 값 입니다.",
            },
        ];

        setLi(liArr);
    }, []);

    return (
        <CommonModal
            list={li}
            mode={mode}
            data={data}
            close={close}
            insertToUpdate={insertToUpdate}
            deleteDisabled={true}
            deleteAuth={getUserRole() == "ROLE_ADM"}
            updateAuth={getUserRole() == "ROLE_ADM"}
        />
    );
}

export default ContentPasswordModal;
