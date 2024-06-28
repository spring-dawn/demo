import React, { useEffect, useState } from "react";
import Swal from "sweetalert2";

import msg from "../../common/message";
import CommonModal from "../../common/CommonModal";
import axios from "axios";
import { getUserRole } from "../../../CommonFunction";
function FeedbackModal(props) {
    const { mode, data, close, render, allData, retry, sgg, year } = props;
    const [files, setFiles] = useState([]);

    const insertToUpdate = (formData) => {
        if (formData !== undefined) {
            if (mode === "insert") {
                return axios.post("/api/system/feedback", formData).then(() => {
                    Swal.fire(msg.alertMessage["insert_success"]);
                    close(false);
                    render();
                });
            } else if (mode == "update") {
                return axios
                    .patch("/api/system/feedback", formData, {
                        withCredentials: true,
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
        if (!del.del) return;

        Swal.fire(msg.alertMessage["double_check"]).then((res) => {
            if (res.isConfirmed) {
                axios
                    .delete("/api/system/feedback/" + del.id.id)
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
                id: "title",
                label: "제목",
                type: "input",
                col: "12",
            },
            {
                id: "contents",
                label: "요청사항",
                type: "textarea",
                col: "12",
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
            deleteAuth={getUserRole() == "ROLE_ADM"}
            updateAuth={getUserRole() == "ROLE_ADM"}
        />
    );
}

export default FeedbackModal;
