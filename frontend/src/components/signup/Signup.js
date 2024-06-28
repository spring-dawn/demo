import React, { useState, useEffect } from "react";
import { Link, useNavigate } from "react-router-dom";
import { useForm } from "react-hook-form";
import Swal from "sweetalert2";

import msg from "../common/message";
import axios from "axios";
import { useCodeTree } from "../../CommonHook";

function Signup(props) {
    const {
        handleSubmit,
        register,
        formState: { errors },
    } = useForm();

    const [step, setStep] = useState(0);
    const { tree: sgg, set: setSgg } = useCodeTree({ parentNm: "31000", deps: [] });
    const navigate = useNavigate();
    // const onClickNextStep = (e) => {
    //     e.preventDefault();
    //
    //     if (!agreeChk) {
    //         alert("약관동의는 필수체크사항입니다.");
    //         return;
    //     }
    //
    //     setStep(step + 1);
    // };
    // const onClickPrevStep = (e) => setStep(step - 1);

    // useEffect(() => {
    //     let stepH = document.querySelectorAll(".signupStep li");
    //     let stepB = document.querySelectorAll(".inner_body");
    //
    //     // 비활성화
    //     stepH.forEach((el) => el.classList.remove("on"));
    //     stepB.forEach((el) => el.classList.remove("on"));
    //
    //     // 활성화
    //     stepH[step].classList.add("on");
    //     stepB[step].classList.add("on");
    //
    //     // 버튼
    //     if (step !== 2) {
    //         document.querySelector(".btn_prev").style.display = step === 0 ? "none" : "inline-block"; // 이전
    //         document.querySelector(".btn_next").style.display = step === 1 ? "none" : "inline-block"; // 다음
    //         document.querySelector(".btn_save").style.display = step === 1 ? "inline-block" : "none"; // 저장
    //     }
    // }, [step]);

    // 동의체크 확인
    const [agreeChk, onAgreeChk] = useState(false);

    // 중복 확인
    const [chk, onChk] = useState(false);
    const onCheckId = (e) => {
        e.preventDefault();

        let id = document.querySelector("#userId").value;
        if (id === "") return alert("아이디를 입력해주세요.");

        axios("/api/system/user/isDuplicate/" + id).then((res) => {
            if (res.data == false) {
                alert("사용 가능한 아이디 입니다.");
                onChk(true);
            } else {
                alert("이미 사용중인 아이디입니다.");
                // Swal.fire(msg.alertMessage[res.status]);
                // document.querySelector("#userId").value = "";
                onChk(false);
            }
        });
    };

    // 저장
    const onSubmit = (data) => {
        if (data !== undefined) {
            if (!chk) {
                return alert("아이디 중복여부를 확인해주세요.");
            }

            if (document.querySelector("#password").value !== document.querySelector("#passwordConfirm").value) {
                return Swal.fire(msg.alertMessage["password_diff"]);
            }

            axios
                .post("/api/system/user/signup", data, {
                    withCredentials: true,
                    headers: {
                        "Content-Type": "application/json",
                    },
                })
                .then(() => {
                    alert(data.userId + " 계정으로 로그인할 수 있습니다. 로그인 화면으로 이동합니다.");
                    return navigate("/login");
                    // setStep(step + 1);
                })
                .catch((err) => alert(err.response.data.message)); // 서버의 예외 메시지를 전달합니다
        }
    };

    return (
        <>
            <nav className="navbar flxCnt" id="gnb">
                <h1 className="navbar-header" id="logo">
                    <a href="/">
                        <img src={require("../../assets/img/common/ul_logo2.png")} />
                        스마트주차행정시스템
                    </a>
                </h1>
            </nav>

            <div className="container">
                <div className="signupWrap">
                    <h2 className="tit">회원가입</h2>

                    {/*<div className="signupStep">*/}
                    {/*    <ol className="_row">*/}
                    {/*        <li className="_col _col4">*/}
                    {/*            <FontAwesomeIcon icon={faListCheck} />*/}
                    {/*            약관동의*/}
                    {/*        </li>*/}
                    {/*        /!* <li>본인인증</li> *!/*/}
                    {/*        <li className="_col _col4">*/}
                    {/*            <FontAwesomeIcon icon={faPenToSquare} />*/}
                    {/*            정보입력*/}
                    {/*        </li>*/}
                    {/*        <li className="_col _col4">*/}
                    {/*            <FontAwesomeIcon icon={faCircleCheck} />*/}
                    {/*            가입완료*/}
                    {/*        </li>*/}
                    {/*    </ol>*/}
                    {/*</div>*/}

                    <div className="signupInner">
                        <form onSubmit={handleSubmit(onSubmit)}>
                            {/*<div className="inner_body on">*/}
                            {/*    <AgreeDoc agreeChk={onAgreeChk} />*/}
                            {/*</div>*/}

                            <div className="inner_body">
                                <ul className="_row">
                                    <li className="_col _col9">
                                        <div className="form_group">
                                            <label htmlFor="userId" className="required">
                                                아이디
                                            </label>
                                            <div className="input_group" style={{ width: "calc(100% - 20em)" }}>
                                                <input
                                                    type="text"
                                                    id="userId"
                                                    className="form_control"
                                                    {...register("userId", {
                                                        required: "아이디를 입력해주세요",
                                                        onChange: (e) => onChk(false),
                                                    })}
                                                />
                                                <span className={errors.userId !== undefined ? "help_block" : ""}>
                                                    {errors.userId?.message}
                                                </span>
                                            </div>

                                            <button className="btn btn_check" onClick={onCheckId}>
                                                중복확인
                                            </button>
                                        </div>
                                    </li>
                                    <li className="_col _col9">
                                        <div className="form_group">
                                            <label htmlFor="password" className="required">
                                                비밀번호
                                            </label>
                                            <div className="input_group">
                                                <input
                                                    type="password"
                                                    id="password"
                                                    className="form_control"
                                                    {...register("password", {
                                                        required: "필수 입력 값 입니다.",
                                                        pattern: msg.inputValid.password,
                                                    })}
                                                    placeholder="비밀번호는 영문, 특수문자, 숫자를 조합하여 9~20자리여야 합니다"
                                                />
                                                <span className={errors.password !== undefined ? "help_block" : ""}>
                                                    {errors.password?.message}
                                                </span>
                                            </div>
                                        </div>
                                    </li>
                                    <li className="_col _col9">
                                        <div className="form_group">
                                            <label htmlFor="passwordConfirm" className="required">
                                                비밀번호 확인
                                            </label>
                                            <div className="input_group">
                                                <input
                                                    type="password"
                                                    id="passwordConfirm"
                                                    className="form_control"
                                                    {...register("passwordConfirm", {
                                                        required: "필수 입력 값 입니다.",
                                                    })}
                                                />
                                                <span
                                                    className={errors.passwordConfirm !== undefined ? "help_block" : ""}
                                                >
                                                    {errors.passwordConfirm?.message}
                                                </span>
                                            </div>
                                        </div>
                                    </li>
                                    <li className="_col _col9">
                                        <div className="form_group">
                                            <label htmlFor="userNm">이름</label>
                                            <div className="input_group">
                                                <input
                                                    type="text"
                                                    id="userNm"
                                                    className="form_control"
                                                    {...register("userNm")}
                                                />
                                                <span className={errors.userNm !== undefined ? "help_block" : ""}>
                                                    {errors.userNm?.message}
                                                </span>
                                            </div>
                                        </div>
                                    </li>
                                    <li className="_col _col9">
                                        <div className="form_group">
                                            <label htmlFor="agency" className="required">
                                                소속기관
                                            </label>
                                            <div className="input_group">
                                                <select
                                                    id={"agency"}
                                                    className="form_control"
                                                    {...register("agency", {
                                                        required: "필수 입력 값 입니다.",
                                                    })}
                                                >
                                                    <option value="31000">{`본청`}</option>
                                                    {sgg.map((ele) => {
                                                        return (
                                                            <option key={ele.id} value={ele.name}>
                                                                {`${ele.value}청`}
                                                            </option>
                                                        );
                                                    })}
                                                </select>
                                                {/*<input*/}
                                                {/*    type="text"*/}
                                                {/*    id="agency"*/}
                                                {/*    className="form_control"*/}
                                                {/*    {...register("agency")}*/}
                                                {/*/>*/}
                                                <span className={errors.agency !== undefined ? "help_block" : ""}>
                                                    {errors.agency?.message}
                                                </span>
                                            </div>
                                        </div>
                                    </li>
                                    <li className="_col _col9">
                                        <div className="form_group">
                                            <label htmlFor="dept" className="required">
                                                부서
                                            </label>
                                            <div className="input_group">
                                                <input
                                                    type="text"
                                                    id="dept"
                                                    className="form_control"
                                                    {...register("dept", {
                                                        required: "필수 입력 값 입니다.",
                                                    })}
                                                />
                                                <span className={errors.dept !== undefined ? "help_block" : ""}>
                                                    {errors.dept?.message}
                                                </span>
                                            </div>
                                        </div>
                                    </li>
                                    <li className="_col _col9">
                                        <div className="form_group">
                                            <label htmlFor="roleReqMsg">요청권한</label>
                                            <div className="input_group">
                                                <input
                                                    type="text"
                                                    id="roleReqMsg"
                                                    className="form_control"
                                                    {...register("roleReqMsg")}
                                                />
                                                <span className={errors.roleReqMsg !== undefined ? "help_block" : ""}>
                                                    {errors.roleReqMsg?.message}
                                                </span>
                                            </div>
                                        </div>
                                    </li>
                                    <li className="_col _col9">
                                        <div className="form_group">
                                            <label htmlFor="email" className="required">
                                                이메일
                                            </label>
                                            <div className="input_group">
                                                <input
                                                    type="text"
                                                    id="email"
                                                    className="form_control"
                                                    {...register("email", {
                                                        required: "필수 입력값 입니다.",
                                                        pattern: msg.inputValid.email,
                                                    })}
                                                    placeholder="email@email.com"
                                                />
                                                <span className={errors.email !== undefined ? "help_block" : ""}>
                                                    {errors.email?.message}
                                                </span>
                                            </div>
                                        </div>
                                    </li>
                                    <li className="_col _col9">
                                        <div className="form_group">
                                            <label htmlFor="cellNo">연락처</label>
                                            <div className="input_group">
                                                <input
                                                    type="text"
                                                    id="cellNo"
                                                    className="form_control"
                                                    {...register("cellNo", {
                                                        pattern: msg.inputValid.cellNo,
                                                    })}
                                                    // placeholder="'-'을 생략하고 숫자만 입력해주세요."
                                                    placeholder="000-000-0000"
                                                />
                                                <span className={errors.cellNo !== undefined ? "help_block" : ""}>
                                                    {errors.cellNo?.message}
                                                </span>
                                            </div>
                                        </div>
                                    </li>
                                </ul>
                            </div>

                            {/*<div className="inner_body">*/}
                            {/*    <div className="infoTxt">가입이 완료되었습니다.</div>*/}

                            {/*    <div className="btnWrap">*/}
                            {/*        <Link to="/main">메인으로 가기</Link>*/}
                            {/*    </div>*/}
                            {/*</div>*/}

                            <div className="inner_footer" style={{ display: "block" }}>
                                {/*<button className="btn btn_prev" onClick={onClickPrevStep}>*/}
                                {/*    <FontAwesomeIcon icon={faAngleLeft} style={{ marginRight: "1em" }} />*/}
                                {/*    이전*/}
                                {/*</button>*/}
                                {/*<button className="btn btn_next" onClick={onClickNextStep}>*/}
                                {/*    다음 <FontAwesomeIcon icon={faAngleRight} style={{ marginLeft: "1em" }} />*/}
                                {/*</button>*/}
                                <button className="btn btn_move_login" type="button" onClick={() => navigate("/login")}>
                                    로그인 이동
                                </button>
                                <button className="btn btn_save" type="submit">
                                    회원가입
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </>
    );
}

export default Signup;
