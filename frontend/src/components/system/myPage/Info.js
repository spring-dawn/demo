import React, { useEffect, useState } from "react";
import { useForm } from "react-hook-form";
import axios from "axios";
import { Button, Checkbox, notification, Form, Input } from "antd";
import { DownOutlined } from "@ant-design/icons";
import Swal from "sweetalert2";
import msg from "../../common/message";
import { useCodeTree } from "../../../CommonHook";

function Info() {
    // 로그인한 user의 개인정보 데이터를 받아옴
    // 사용자명, 아이디, 이메일, 연락처, 소속, 부서, 비밀번호 정보 나타냄
    // 비밀번호, 비밀번호 확인 입력하면 -> 이 두개가 일치하는지 확인함 => 밑에 글씨로 나타내기

    const storedUserInfo = localStorage.getItem("user");
    const userInfo = JSON.parse(storedUserInfo);

    const {
        register,
        handleSubmit,
        getValues,
        reset,
        formState: { errors },
    } = useForm({
        defaultValues: userInfo,
    });

    const { tree: sgg, set: setSgg } = useCodeTree({ parentNm: "31000", deps: [] });

    // 사용자가 입력한 비밀번호 값
    const [pw, setPw] = useState("");

    // 사용자가 입력한 비밀번호 확인 값
    const [confirmPw, setConfirmPw] = useState("");

    const handleButtonClick = (formData) => {
        if (formData.password !== formData.passwordConfirm) {
            return Swal.fire(msg.alertMessage["password_diff"]);
        }

        axios
            .patch("/api/system/user/users", formData)
            .then((response) => {
                localStorage.setItem("user", JSON.stringify(response.data));
                alert("개인정보 수정이 완료되었습니다.");
            })
            .catch((error) => {
                alert(error.response.data.message);
            });
    };

    useEffect(() => {
        reset(userInfo);
    }, [sgg]);

    return (
        <div id="infoPage">
            <div className="input_wrap">
                <h2>개인정보 조회</h2>
                <form className="custom-form" autoComplete="off" onSubmit={handleSubmit(handleButtonClick)}>
                    <div className="form-ele">
                        <label>아이디</label>
                        <input label="아이디" {...register("userId")} readOnly />
                    </div>

                    <div className="form-ele">
                        <label>사용자명</label>
                        <input label="이름" {...register("userNm")} />
                    </div>

                    <div className="form-ele">
                        <label className="required" htmlFor="password">
                            비밀번호
                        </label>
                        <input
                            id="password"
                            type="password"
                            {...register("password", {
                                required: "비밀번호는 필수 입력입니다.",
                                // pattern: msg.inputValid.password,
                            })}
                            // placeholder="비밀번호는 영문, 특수문자, 숫자를 조합하여 9~20자리여야 합니다"
                            // onChange={(e) => setPw(e.target.value)}
                        />
                        <span className={errors.password !== undefined ? "help_block" : ""}>
                            {errors.password?.message}
                        </span>
                    </div>

                    <div className="form-ele">
                        <label className="required">비밀번호 확인</label>
                        <input
                            type="password"
                            label="비밀번호 확인"
                            {...register("passwordConfirm")}
                            onChange={(e) => setConfirmPw(e.target.value)}
                        />
                    </div>

                    <div className="form-ele">
                        <label htmlFor="email" className="required">
                            이메일
                        </label>
                        <input
                            id="email"
                            {...register("email", {
                                required: "이메일은 필수 입력입니다.",
                                pattern: msg.inputValid.email,
                            })}
                        />
                        <span className={errors.email !== undefined ? "help_block" : ""}>{errors.email?.message}</span>
                    </div>

                    <div className="form-ele">
                        <label>전화번호</label>
                        <input label="전화번호" {...register("cellNo")} />
                    </div>

                    <div className="form-ele">
                        <label className="required">소속</label>
                        <select id={"agency"} className="form_control" {...register("agency")}>
                            <option value="31000">{`본청`}</option>
                            {sgg.map((ele) => {
                                return (
                                    <option key={ele.id} value={ele.name}>
                                        {`${ele.value}청`}
                                    </option>
                                );
                            })}
                        </select>
                    </div>

                    <div className="form-ele">
                        <label htmlFor="dept" className="required">
                            부서
                        </label>
                        <input
                            id="dept"
                            label="부서"
                            {...register("dept", {
                                required: "부서는 필수 입력입니다.",
                            })}
                        />
                        <span className={errors.dept !== undefined ? "help_block" : ""}>{errors.dept?.message}</span>
                    </div>

                    <div className="form-ele">
                        <label>권한명</label>
                        <input label="권한명" {...register("roleEncodedNm")} readOnly />
                    </div>

                    <div className="form-ele">
                        <label>가입일</label>
                        <input label="가입일" {...register("joinDt")} readOnly />
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

export default Info;
