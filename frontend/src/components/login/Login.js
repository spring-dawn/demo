import React, { useState, useEffect } from "react";
import { Link } from "react-router-dom";
import { useForm } from "react-hook-form";
import Swal from "sweetalert2";
import FindPassword from "./FindPassword";
import msg from "../common/message";
import axios from "axios";

function Login(props) {
    const {
        register,
        formState: { errors },
        handleSubmit,
        getValues,
        reset,
        getFieldState,
    } = useForm();

    const onSubmit = (data) => {
        fetch("/api/signin", {
            method: "post",
            credentials: "include",
            headers: {
                "Content-Type": "application/x-www-form-urlencoded",
            },
            body: new URLSearchParams(data),
        })
            .then((res) => {
                return res.json();
            })
            .then((data) => {
                if (data.code === "login_failed") {
                    // 결과 메시지의 개행문자를 <br> 로 치환, html 메시지로 표현합니다. 기타 가공 가능.
                    let resultMsg = data.message;

                    return Swal.fire({
                        title: "Error!",
                        icon: "error",
                        html: resultMsg.replace(/\n/g, "<br>"),
                    });
                } else {
                    Swal.fire(msg.alertMessage[data.code]).then((res) => {
                        // [240307] 로그인 이력 로깅
                        axios("/api/system/log/login").catch((err) => {
                            console.log(err.response.data.message);
                        });

                        localStorage.setItem("isAuthorized", "Y");
                        // 사용자 정보 저장
                        localStorage.setItem("user", JSON.stringify(data));
                        document.location.href = "/";
                    });
                }
            });
    };

    const [show, setShow] = useState(false);
    const onFindPW = () => {
        setShow(true);
    };
    const onSignin = () => {
        setShow(false);
    };

    return (
        <>
            <div className="loginWrap">
                <ul>
                    <li className={show === false ? "on" : ""}>
                        <div className="loginInner">
                            <form onSubmit={handleSubmit(onSubmit)}>
                                <div className="inner_header">
                                    <img src={require("../../assets/img/common/loginBg.jpg")} />
                                    <h2>Sign In</h2>
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
                                                {...register("userId", { required: "아이디를 입력해주세요" })}
                                            />
                                            <span className={errors.userId !== undefined ? "help_block" : ""}>
                                                {errors.userId?.message}
                                            </span>
                                        </div>
                                    </div>

                                    <div className="form_group">
                                        <label htmlFor="password">비밀번호</label>
                                        <div className="input_group">
                                            <input
                                                type="password"
                                                id="password"
                                                className="form_control"
                                                placeholder="비밀번호를 입력해주세요."
                                                {...register("password", { required: "비밀번호를 입력해주세요." })}
                                            />
                                            <span className={errors.password !== undefined ? "help_block" : ""}>
                                                {errors.password?.message}
                                            </span>
                                        </div>
                                    </div>

                                    <ul className="loginDtlWrap">
                                        <li>
                                            <input
                                                name="autoLogin"
                                                type="checkbox"
                                                {...register("autoLogin")}
                                                id="autoLogin"
                                            />
                                            <label htmlFor="autoLogin">Remember me</label>
                                        </li>
                                        <li>
                                            <a href="#" onClick={onFindPW}>
                                                Forget Password?
                                            </a>
                                        </li>
                                    </ul>
                                </div>
                                <div className="inner_footer">
                                    <button className="btn btn_login" type="submit">
                                        로그인
                                    </button>

                                    <div className="signUpWrap">
                                        <Link to="/signup">회원가입</Link>
                                    </div>
                                </div>
                            </form>
                        </div>
                    </li>

                    <li className={show === true ? "on" : ""}>
                        <div className="findInner">
                            <FindPassword show={show} />

                            <div style={{ position: "absolute", bottom: "5em" }}>
                                <a href="#" onClick={onSignin}>
                                    {/* <FontAwesomeIcon icon={faArrowLeftLong} /> */}
                                    Back
                                </a>
                            </div>
                        </div>
                    </li>
                </ul>
            </div>
        </>
    );
}

export default Login;
