import React, { useEffect, useState } from "react";
import { useForm } from "react-hook-form";
import axios from "axios";
import { Button, Checkbox, notification, Form, Input } from "antd";
import { DownOutlined } from "@ant-design/icons";
import Swal from "sweetalert2";
import msg from "../../common/message";

function Pw() {
    const {
        register,
        handleSubmit,
        getValues,
        formState: { errors },
    } = useForm();

    const storedUserInfo = localStorage.getItem("user");
    const userInfo = JSON.parse(storedUserInfo);

    // 사용자가 입력한 새 비밀번호 값
    const [newpw, setNewpw] = useState("");

    // 사용자가 입력한 새 비밀번호 확인 값
    const [confirmNewpw, setConfirmNewpw] = useState("");

    const handleButtonClick = (formData) => {
        if (formData.passwordNew !== formData.passwordNewConfirm) {
            return Swal.fire(msg.alertMessage["password_diff"]);
        }

        axios
            .patch("/api/system/user/users/dtl", formData)
            .then((response) => {
                localStorage.setItem("user", JSON.stringify(response.data));
                alert("비밀번호 변경이 완료되었습니다.");
            })
            .catch((error) => {
                alert(error.response.data.message);
            });
    };

    return (
        <div id="pwPage">
            <div className="input_wrap">
                <h2>비밀번호 변경</h2>
                <form className="custom-form" autoComplete="off" onSubmit={handleSubmit(handleButtonClick)}>
                    <div className="form-ele">
                        <label>아이디</label>
                        <input label="아이디" defaultValue={userInfo.userId} {...register("userId")} readOnly />
                    </div>

                    {/*<div className="form-ele">*/}
                    {/*    <label>사용자명</label>*/}
                    {/*    <input label="이름" defaultValue={userInfo.userNm} {...register("userNm")} />*/}
                    {/*</div>*/}

                    <div className="form-ele">
                        <label className="required">비밀번호</label>
                        <input
                            type="password"
                            label="비밀번호"
                            {...register("password", {
                                required: "현재 비밀번호를 입력해주세요.",
                                // pattern: msg.inputValid.password,
                            })}
                        />
                        <span className={errors.password !== undefined ? "help_block" : ""}>
                            {errors.password?.message}
                        </span>
                    </div>

                    <div className="form-ele">
                        <label className="required">새 비밀번호</label>
                        <input
                            type="password"
                            label="비밀번호"
                            {...register("passwordNew", {
                                required: "새 비밀번호를 입력해주세요",
                            })}
                        />
                        <span className={errors.passwordNew !== undefined ? "help_block" : ""}>
                            {errors.passwordNew?.message}
                        </span>
                    </div>

                    <div className="form-ele">
                        <label className="required">비밀번호 확인</label>
                        <input
                            type="password"
                            label="비밀번호 확인"
                            {...register("passwordNewConfirm")}
                            onChange={(e) => setConfirmNewpw(e.target.value)}
                        />
                    </div>

                    <div className="btm_wrap">
                        <button className="btn btn_save" type="submit">
                            수 정
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
}

export default Pw;
