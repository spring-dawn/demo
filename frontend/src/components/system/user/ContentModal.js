import React, { useEffect, useState } from "react";
import { useForm } from "react-hook-form";
import Swal from "sweetalert2";

import msg from "../../common/message";
import CommonModal from "./CommonModal";

function ContentModal(props) {
    const { mode, data, close } = props;
    const {
        handleSubmit,
        register,
        formState: { errors },
        reset,
        getValues,
        setValue,
    } = useForm();

    // 권한 옵션
    const options = [
        { value: "3", label: "ROLE_ADMIN" },
        { value: "4", label: "ROLE_USER" },
    ];

    const onSubmit = (data) => {
        if (!chk) {
            alert("중복확인해주세요");
            return;
        }

        if (getValues("password") !== getValues("passwordConfirm")) {
            Swal.fire(msg.alertMessage["password_diff"]);
            return;
        }

        fetch("/api/system/user/signup", {
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
    };

    // 중복 확인
    const [chk, onChk] = useState(false);
    const onCheckId = (e) => {
        e.preventDefault();
        let id = getValues("userId");
        if (id === "") {
            alert("아이디를 입력해주세요.");
            return;
        }

        fetch("/api/system/user/users/" + id, {
            method: "HEAD",
        }).then((res) => {
            if (res.status === 200) {
                Swal.fire(msg.alertMessage[res.status]);
                setValue("userId", "");
                onChk(false);
            } else {
                alert("사용 가능한 아이디 입니다.");
                onChk(true);
            }
        });
    };

    useEffect(() => {
        if (data) {
            reset(data);
        }
    }, [data, reset]);

    const [readOnly, setReadOnly] = useState(false);
    if (mode === "update") {
        useEffect(() => {
            setReadOnly(true);
        }, [mode]);
    }

    // 삭제
    const [del, setDel] = useState(false);
    const btnDelete = (e) => {
        e.preventDefault();

        // 유효성 뛰어넘기
        setDel(true);
        onChk(true);

        let id = getValues("userId");
        Swal.fire(msg.alertMessage["double_check"]).then((res) => {
            if (res.isConfirmed) {
                fetch("/api/system/user/users/" + id, {
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
                setDel(false);
                return;
            }
        });
    };

    return (
        <form onSubmit={handleSubmit(onSubmit)}>
            <div className="modal_body">
                <ul className="_row">
                    <li className="_col_gap _col6">
                        <div className="form_group">
                            <label htmlFor="userId">
                                아이디
                                <span className="required" />
                            </label>
                            <div className="input_group">
                                <input
                                    type="text"
                                    id="userId"
                                    className="form_control"
                                    {...register("userId", {
                                        required: del ? false : "아이디를 입력해주세요",
                                        onChange: (e) => onChk(false),
                                    })}
                                    readOnly={readOnly}
                                />
                                <span className={errors.userId !== undefined ? "help_block" : ""}>
                                    {errors.userId?.message}
                                </span>
                            </div>
                        </div>
                    </li>

                    <li className="_col_gap _col6">
                        <div className="form_group">
                            {mode === "update" ? (
                                ""
                            ) : (
                                <button className="btn btn_check" onClick={onCheckId}>
                                    중복 확인
                                </button>
                            )}
                        </div>
                    </li>

                    <li className="_col_gap _col6">
                        <div className="form_group">
                            <label htmlFor="password">
                                비밀번호
                                <span className="required" />
                            </label>
                            <div className="input_group">
                                <input
                                    type="password"
                                    id="password"
                                    className="form_control"
                                    placeholder="영문, 숫자, 특수문자"
                                    {...register("password", {
                                        required: del ? false : "필수 입력 값 입니다.",
                                        // required: "8자리 이상..????? 특수문자 ...? 입력해주세요",
                                    })}
                                />
                                <span className={errors.password !== undefined ? "help_block" : ""}>
                                    {errors.password?.message}
                                </span>
                            </div>
                        </div>
                    </li>
                    <li className="_col_gap _col6">
                        <div className="form_group">
                            <label htmlFor="passwordConfirm">
                                비밀번호 확인
                                <span className="required" />
                            </label>
                            <div className="input_group">
                                <input
                                    type="password"
                                    id="passwordConfirm"
                                    className="form_control"
                                    {...register("passwordConfirm", { required: del ? false : "필수 입력 값 입니다." })}
                                />
                                <span className={errors.passwordConfirm !== undefined ? "help_block" : ""}>
                                    {errors.passwordConfirm?.message}
                                </span>
                            </div>
                        </div>
                    </li>
                    <li className="_col_gap _col6">
                        <div className="form_group">
                            <label htmlFor="userNm">사용자명</label>
                            <div className="input_group">
                                <input type="text" id="userNm" className="form_control" {...register("userNm")} />
                                <span className={errors.userNm !== undefined ? "help_block" : ""}>
                                    {errors.userNm?.message}
                                </span>
                            </div>
                        </div>
                    </li>
                    <li className="_col_gap _col6">
                        <div className="form_group">
                            <label htmlFor="email">
                                이메일
                                <span className="required" />
                            </label>
                            <div className="input_group">
                                <input
                                    type="text"
                                    id="email"
                                    className="form_control"
                                    placeholder="email@email.co.kr"
                                    {...register("email", {
                                        required: del ? false : "필수 입력값 입니다.",
                                        pattern: msg.inputValid.email,
                                    })}
                                />
                                <span className={errors.email !== undefined ? "help_block" : ""}>
                                    {errors.email?.message}
                                </span>
                            </div>
                        </div>
                    </li>
                    <li className="_col_gap _col6">
                        <div className="form_group">
                            <label htmlFor="cellNo">연락처</label>
                            <div className="input_group">
                                <input
                                    type="text"
                                    id="cellNo"
                                    className="form_control"
                                    placeholder="000-0000-0000"
                                    {...register("cellNo", {
                                        // required: "필수 입력값 입니다.",
                                        pattern: msg.inputValid.cellNo,
                                    })}
                                />
                                <span className={errors.cellNo !== undefined ? "help_block" : ""}>
                                    {errors.cellNo?.message}
                                </span>
                            </div>
                        </div>
                    </li>
                    <li className="_col_gap _col6">
                        <div className="form_group">
                            <label htmlFor="authCd">권한</label>
                            <div className="input_group">
                                <select
                                    id="authCd"
                                    className="form_control"
                                    defaultValue="3"
                                    {...register("authCd", { required: "필수 입력 값 입니다." })}
                                >
                                    {options.map((m) => (
                                        <option key={m.label} value={m.value}>
                                            {m.label}
                                        </option>
                                    ))}
                                </select>
                            </div>
                        </div>
                    </li>
                    <li className="_col_gap _col6">
                        <div className="form_group">
                            <label htmlFor="useYn">사용여부</label>
                            <div className="input_group">
                                <select
                                    id="useYn"
                                    className="form_control"
                                    defaultValue="Y"
                                    {...register("useYn", { required: "필수 입력 값 입니다." })}
                                >
                                    <option value="Y">사용</option>
                                    <option value="N">미사용</option>
                                </select>
                            </div>
                        </div>
                    </li>
                    <li className="_col_gap _col12">
                        <div className="form_group">
                            <label htmlFor="comment">비고</label>
                            <div className="input_group">
                                <textarea
                                    id="comment"
                                    rows="3"
                                    className="form_control"
                                    {...register("comment")}
                                ></textarea>
                            </div>
                        </div>
                    </li>
                </ul>
            </div>
            <footer>
                <div className="btnWrap">
                    {mode === "update" ? (
                        <button className="btn btn_delete" onClick={btnDelete}>
                            삭제
                        </button>
                    ) : null}
                    <button className="btn btn_close" onClick={close}>
                        닫기
                    </button>
                    <button className="btn btn_save" type="submit">
                        {mode === "update" ? "수정" : "등록"}
                    </button>
                </div>
            </footer>
        </form>
    );
}

export default ContentModal;
