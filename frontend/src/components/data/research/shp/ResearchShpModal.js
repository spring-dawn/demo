import React, { useEffect, useState } from "react";
import Swal from "sweetalert2";
import msg from "../../../common/message";
import CommonModal from "../../../common/CommonModal";
import { useCodeTree } from "../../../../CommonHook";
import axios from "axios";
import { getUserRole } from "../../../../CommonFunction";

function ResearchShpModal(props) {
    const { mode, data, close, render, allData, retry, sgg, year, type } = props;
    const [files, setFiles] = useState([]);

    // 저장
    const insertToUpdate = (formData) => {
        if (formData !== undefined) {
            if (mode === "insert") {
                formData = {
                    ...formData,
                    files: files,
                };

                let submitFormData = new FormData();

                Object.keys(formData).forEach((key) => {
                    if (key == "files") {
                        [...formData[key]].forEach((file) => {
                            submitFormData.append("files", file); // Append files without an index
                        });
                    } else {
                        submitFormData.append(key, formData[key] || "");
                    }
                });

                return axios
                    .post("/api/gis/shp-result?retry=" + (retry || false), submitFormData, {
                        headers: {
                            "Content-Type": "multipart/form-data",
                        },
                    })
                    .then(({ data }) => {
                        close(false);
                        render();
                    });
            } else if (mode == "update") {
                formData = {
                    resultNo: formData.resultNo,
                    name: formData.name,
                    regCode: formData.regCode,
                    year: formData.year,
                    type: formData.type,
                    subType: formData.subType,
                    rmrk: formData.rmrk,
                    viewYn: formData.viewYn,
                    cardYn: formData.cardYn,
                    epsg: formData.epsg,
                };

                return axios
                    .patch("/api/gis/shp", JSON.stringify(formData), {
                        headers: {
                            "Content-Type": "application/json",
                        },
                    })
                    .then((json) => {
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
        if (del.del) {
            Swal.fire(msg.alertMessage["double_check"]).then((res) => {
                if (res.isConfirmed) {
                    fetch("/api/gis/shp?resultNo=" + del.id.resultNo, {
                        method: "DELETE",
                    })
                        .then((res) => {
                            if (res.ok) {
                                Swal.fire(msg.alertMessage["delete"]);
                                close(false);
                                render();
                            } else {
                                return res.json().then((json) => {
                                    throw new Error(json.message);
                                });
                            }
                        })
                        .catch((error) => {
                            Swal.fire({
                                text: error.message,
                                content: {
                                    element: "span",
                                    attributes: {
                                        style: "font-size: 12px;",
                                    },
                                },
                            });
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
    const [typeData, setTypeData] = useState(data.type ? data.type : "베이스");
    const [subTypeData, setSubTypeData] = useState(data.subType ? data.subType : "인구");
    const [cardChk, setCardChk] = useState(data.cardYn ? data.cardYn : "Y");

    // 서브타입 코드 가져오기
    const { tree: optionList, set: setOptionList } = useCodeTree({ parentNm: "SR", deps: [] });

    useEffect(() => {
        let liArr = [
            {
                id: "name",
                label: "데이터명",
                type: "input",
                input_type: "text",
                col: "12",
                required: true,
                msg: "필수입력 값 입니다.",
            },
            {
                id: "rmrk",
                label: "설명",
                type: "textarea",
                col: "12",
            },
            {
                id: "year",
                label: "연도",
                type: "yearPicker",
                col: "6",
            },
            {
                id: "regCode",
                label: "구군",
                type: "select",
                option:
                    sgg.length > 0
                        ? sgg.map((ele) => ({ value: ele.name, label: ele.value }))
                        : [{ value: "", label: "" }],
                col: "6",
            },
            {
                id: "type",
                label: "데이터 구분",
                type: "select",
                option:
                    type.length > 0
                        ? type.map((ele) => ({ value: ele.value, label: ele.value }))
                        : [{ value: "", label: "" }],
                col: "6",
                change: setTypeData,
            },
            {
                id: "subType",
                label: "상세 구분",
                type: "select",
                option:
                    type.length > 0
                        ? type
                              .find((ele) => ele.value == typeData)
                              .children.map((ele) => ({ value: ele.value, label: ele.value }))
                        : [{ value: "", label: "" }],
                col: "6",
                change: (subValue) => {
                    setSubTypeData(subValue);
                },
            },
            {
                id: "cardYn",
                label: "관리카드 표시",
                type: "select",
                disabled: typeData == "베이스" && subTypeData == "블럭경계" ? false : true,
                option:
                    typeData == "베이스" && subTypeData == "블럭경계"
                        ? [
                              { value: "Y", label: "사용" },
                              { value: "N", label: "미사용" },
                          ]
                        : [{ value: "", label: "" }],
                col: "6",
                change: (subValue) => {
                    setCardChk(subValue);
                },
            },
            {
                id: "cardYn_comment",
                type: "comment",
                msg: "*관리카드 표시를 체크할 경우 GIS 관리카드 탭에서 사용됩니다.",
                col: "6",
                disabled: typeData == "베이스" && subTypeData == "블럭경계" ? false : true,
            },
            {
                id: "viewYn",
                label: "GIS시각화",
                type: "select",
                option: [
                    { value: "N", label: "미사용" },
                    { value: "Y", label: "사용" },
                ],
                col: "6",
                disabled: true,
            },
            {
                id: "epsg",
                label: "좌표계",
                type: "input",
                input_type: "text",
                col: "6",
                placeholder: "숫자만 입력해주세요 예) 5187",
                defaultValue: 5187,
            },
            {
                id: "files",
                label: "파일 업로드",
                type: "files",
                col: "6",
                required: true,
                msg: "shp,shx,dbf 파일을 등록해주세요",
                disabled: mode === "update" ? true : false,
                uploadName: "SHP 등록",
                downloadName: "실태조사SHP",
                files: files,
                setFiles: (files) => setFiles(Array.from(files)),
            },
        ];

        setLi(liArr);
    }, [typeData, subTypeData, cardChk, files, optionList]);

    return (
        <CommonModal
            list={li}
            mode={mode}
            retry={retry}
            data={data}
            close={close}
            setDel={setDel}
            insertToUpdate={insertToUpdate}
            deleteAuth={(getUserRole() == "ROLE_ADM" || getUserRole() == "ROLE_SGG_ADM") && data.state != 2}
            updateAuth={getUserRole() == "ROLE_ADM" || getUserRole() == "ROLE_SGG_ADM"}
        />
    );
}

export default ResearchShpModal;
