import React, { useState, useEffect } from "react";
import { useForm } from "react-hook-form";

function AgreeDoc(props) {
    const { agreeChk } = props;

    const {
        register,
        formState: { errors },
        getValues,
    } = useForm();

    const [evt, setEvt] = useState();
    useEffect(() => {
        if (!getValues("chkAgree") || !getValues("chkPrivate")) {
            agreeChk(false);
        } else {
            agreeChk(true);
        }
    }, [evt]);

    return (
        <ul className="_row">
            <li className="_col _col12 on">
                <h4>회원약관 동의</h4>

                <div className="inner_cont scroll">
                    <div className="agreeWrap">
                        <ol>
                            <li>
                                <h5>제 1장 총칙</h5>
                                <dl>
                                    <dt>제 1조(목적)</dt>
                                    <dd>
                                        <b>
                                            본 약관은 주차관리시스템 (이하 "당 사이트")이 제공하는 모든 서비스(이하
                                            "서비스")의 이용조건 및 절차, 이용자와 당 사이트의 권리, 의무, 책임사항과
                                            기타 필요한 사항을 규정함을 목적으로 합니다.
                                        </b>
                                    </dd>
                                </dl>
                                <dl className="order_num">
                                    <dt>제 2 조 (약관의 효력과 변경)</dt>
                                    <dd>
                                        1. 당 사이트는 귀하가 본 약관 내용에 동의하는 것을 조건으로 귀하에게 서비스를
                                        제공할 것이며, 귀하가 본 약관의 내용에 동의하는 경우, 당 사이트의 서비스 제공
                                        행위 및 귀하의 서비스 사용 행위에는 본 약관이 우선적으로 적용될 것입니다.
                                    </dd>
                                    <dd>
                                        2. 당 사이트는 본 약관을 사전 고지 없이 변경할 수 있으며, 변경된 약관은 당
                                        사이트 사이트 내에 공지함으로써 이용자가 직접 확인하도록 할 것입니다. 이용자가
                                        변경된 약관에 동의하지 아니하는 경우, 이용자는 본인의 회원등록을
                                        취소(회원탈퇴)할 수 있으며, 계속 사용의 경우는 약관 변경에 대한 동의로
                                        간주됩니다. 변경된 약관은 공지와 동시에 그 효력이 발생됩니다.
                                    </dd>
                                </dl>
                            </li>
                        </ol>
                    </div>
                </div>

                <div className="form_group" style={{ alignItems: "center" }}>
                    <input
                        type="checkbox"
                        id="chkAgree"
                        className="form_control"
                        {...register("chkAgree", { onChange: (e) => setEvt(e) })}
                    ></input>
                    <label htmlFor="chkAgree" style={{ marginTop: 0 }}>
                        동의합니다.(필수)
                    </label>
                </div>
            </li>

            <li className="_col _col12">
                <h4>개인정보 수집 및 이용동의</h4>

                <div className="inner_cont scroll">
                    <div className="agreeWrap">
                        <ol>
                            <li>
                                <h5>가. 개인정보의 수집 및 이용 목적</h5>

                                <dl>
                                    <dt>
                                        <b>○ 홈페이지 회원 가입 및 관리</b>
                                    </dt>
                                    <dd>
                                        회원 가입의사 확인, 회원제 서비스 제공에 따른 본인 식별·인증, 회원자격
                                        유지·관리, 제한적 본인확인제 시행에 따른 본인확인, 서비스 부정이용 방지, 만 14세
                                        미만 아동의 개인정보 처리시 법정대리인의 동의여부 확인, 각종 고지·통지, 고충처리
                                        등을 목적으로 개인정보를 처리합니다.
                                    </dd>

                                    <dt>
                                        <b>○ 민원사무 처리</b>
                                    </dt>
                                    <dd>
                                        민원인의 신원 확인, 민원사항 확인, 사실조사를 위한 연락·통지, 처리결과 통보 등의
                                        목적으로 개인정보를 처리합니다.
                                    </dd>
                                </dl>
                            </li>

                            <li>
                                <h5>나. 개인정보 수집 항목</h5>
                            </li>
                        </ol>
                    </div>
                </div>

                <div className="form_group" style={{ alignItems: "center" }}>
                    <input
                        type="checkbox"
                        id="chkPrivate"
                        className="form_control"
                        {...register("chkPrivate", {
                            onChange: (e) => setEvt(e),
                        })}
                    ></input>
                    <label htmlFor="chkPrivate" style={{ marginTop: 0 }}>
                        동의합니다.(필수)
                    </label>
                </div>
            </li>
        </ul>
    );
}

export default AgreeDoc;
