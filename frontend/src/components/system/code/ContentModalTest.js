import React, { useEffect, useState } from "react";
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
            fetch("/api/system/code/codes", {
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
                    fetch("/api/system/code/codes/" + del.id, {
                        method: "DELETE",
                    }).then((res) => {
                        if (res.status === 200) {
                            Swal.fire(msg.alertMessage["delete"]);
                            close(true);
                            // ag-grid 갱신
                            document.querySelector(".btn_search").click();
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
    useEffect(() => {
        let liArr = [
            {
                id: "name",
                label: "코드명",
                type: "input",
                input_type: "text",
                col: "6",
                required: true,
                msg: "필수 입력 값 입니다.",
                readonly: mode === "update" ? true : false,
            },
            {
                id: "value",
                label: "코드값",
                type: "input",
                input_type: "text",
                col: "6",
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
    }, []);

    return <CommonModal list={li} mode={mode} data={data} close={close} form={setFormData} setDel={setDel} />;
}

export default ContentModal;
