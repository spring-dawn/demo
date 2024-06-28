import React, { useEffect, useState } from "react";
import { useForm } from "react-hook-form";

import Swal from "sweetalert2";
import msg from "../common/message";

function FindPassword(props) {
    const {
        register,
        formState: { errors },
        handleSubmit,
        reset,
    } = useForm();

    const onSubmit = (data) => {
        // 임시 비밀번호 발급할지 여부 묻고 진행
        Swal.fire(msg.alertMessage["findPw"]).then((res) => {
            if (!res.isConfirmed) return;
            // 신원 확인
            fetch("/api/system/user/whoIam", {
                method: "post",
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify(data),
            })
                .then((res) => res.json())
                .then((data) => {
                    if (data.code === "500") return Swal.fire(msg.alertMessage["req_diff"]);
                    //
                    return Swal.fire(msg.alertMessage["findPwMailing"]);
                });
        });
    };

    useEffect(() => {
        if (!props.show) {
            reset();
        }
    }, [props.show]);

    return (
        <form onSubmit={handleSubmit(onSubmit)}>
            <div className="inner_header">
                <h2>비밀번호 찾기</h2>
            </div>
            <div className="inner_body">
                <div className="form_group">
                    <label htmlFor="userId">아이디</label>
                    <div className="input_group">
                        <input
                            type="text"
                            id="userId"
                            className="form_control"
                            placeholder="아이디를 입력해주세요."
                            {...register("userId", { required: "필수 입력값 입니다." })}
                        />
                        <span className={errors.userId !== undefined ? "help_block" : ""}>
                            {errors.userId?.message}
                        </span>
                    </div>
                </div>

                <div className="form_group">
                    <label htmlFor="email">이메일</label>
                    <div className="input_group">
                        <input
                            type="text"
                            id="email"
                            className="form_control"
                            placeholder="email@email.co.kr"
                            {...register("email", {
                                required: "필수 입력값 입니다.",
                                pattern: msg.inputValid.email,
                            })}
                        />
                        <span className={errors.email !== undefined ? "help_block" : ""}>{errors.email?.message}</span>
                    </div>
                </div>
            </div>
            <div className="inner_footer">
                <button className="btn btn_submit" type="submit">
                    비밀번호찾기
                </button>
            </div>
        </form>
    );
}

export default FindPassword;
