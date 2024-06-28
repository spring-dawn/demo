import React, { useEffect, useState, useCallback } from "react";
import { AgGridReact } from "ag-grid-react";

import ContentSearch from "./ContentSearch";
import Modal from "../../common/Modal";
import { useCodeTree } from "../../../CommonHook";
import ContentInfoModal from "./ContentInfoModal";
import ContentPasswordModal from "./ContentPasswordModal";
import axios from "axios";
import { gugunParseCodeToName } from "../../../CommonFunction";

function User(props) {
    const [render, setRender] = useState(false);
    const [data, setData] = useState();
    const [roles, setRoles] = useState([]);
    const [modalTab, setModalTab] = useState("info");

    // Grid
    const onGridReady = useCallback((params) => {
        params.api.sizeColumnsToFit();
    }, []);

    const userJSON = localStorage.getItem("user");
    let userInfo;

    if (userJSON) {
        userInfo = JSON.parse(userJSON);
    }

    // modal
    const [proc, setProc] = useState({});
    const [modalOpen, setModalOpen] = useState(false);
    const onOpenModal = () => {
        setProc({ task: "proc", mode: "insert" });
        setModalOpen(true);
        setUser({});
    };
    const closeModal = () => {
        setModalOpen(false);
    };
    useEffect(() => {
        // role 목록
        axios("/api/system/role/roles").then((res) => {
            setRoles(res.data);
        });
    }, []);
    // 구군 코드 가져오기
    const { tree: sgg, set: setSgg } = useCodeTree({ parentNm: "31000", deps: [] });
    // 그리드용 매핑 배열
    const sggMap = sgg.reduce((result, item) => {
        result[item.name] = item.value;
        return result;
    }, {});
    const roleMap = roles.reduce((result, item) => {
        result[item.name] = item.encodedNm;
        return result;
    }, {});

    // Example cell Click event
    const [user, setUser] = useState();
    const cellClickedListener = useCallback((evt) => {
        setModalTab("info");
        setUser(evt.data);
        setProc({ task: "proc", mode: "update" });
        setModalOpen(true);
    }, []);

    // column setting
    const defaultColDef = {
        resizable: true,
        autoHeight: true,
    };
    const columnDefs = [
        { field: "seq", headerName: "번호", valueGetter: "node.rowIndex + 1", sortable: true, flex: 0.5 },
        { field: "userId", headerName: "아이디" },
        { field: "userNm", headerName: "성명" },
        {
            field: "roleNm",
            headerName: "권한",
            valueFormatter: (params) => {
                const nm = roleMap[params.value];
                return nm || params.value;
            },
        },
        { field: "email", headerName: "이메일", flex: 1.5 },
        { field: "cellNo", headerName: "연락처", flex: 1 },
        {
            field: "agency",
            headerName: "소속",
            valueFormatter: (params) => {
                const sggNm = sggMap[params.value];
                return sggNm || "본청"; // 31000 인 경우 본청
            },
        },
        // { field: "dept", headerName: "부서" },
        {
            field: "useYn",
            headerName: "상태",
            flex: 0.5,
            valueFormatter: (params) => {
                return params.value == "Y" ? "O" : "X";
            },
            cellStyle: (params) => {
                return {
                    color: params.value == "N" ? "red" : "green",
                    fontWeight: "bold",
                };
            },
        },
        { field: "joinDt", headerName: "가입일자", sortable: true },
    ];

    return (
        <div>
            <div className="pageWrap">
                <ContentSearch setData={setData} sgg={sgg} />
                {/* 23.11.16 관리자의 사용자 등록 기능 삭제 */}
                {/* <div className="btnWrap flxRit">
                    <button className="btn btn_write" onClick={onOpenModal}>
                        등록
                    </button>
                </div> */}

                <div className="tableWrap">
                    <div className="ag-theme-alpine" style={{ width: "100%", height: "540px" }}>
                        <AgGridReact
                            headerHeight={40}
                            rowData={data}
                            columnDefs={columnDefs}
                            defaultColDef={defaultColDef}
                            rowSelection="multiple"
                            onGridReady={onGridReady}
                            onCellClicked={cellClickedListener}
                            pagination={true}
                            paginationPageSize={10}
                        />
                    </div>
                </div>
            </div>

            <Modal
                open={modalOpen}
                close={closeModal}
                header="사용자관리"
                selectTab={modalTab}
                tab={
                    user?.userId == userInfo?.userId
                        ? [
                              { name: "사용자 관리", value: "info", onClick: () => setModalTab("info") },
                              { name: "비밀번호 변경", value: "password", onClick: () => setModalTab("password") },
                          ]
                        : undefined
                }
            >
                {modalTab == "info" ? (
                    <ContentInfoModal
                        mode={proc.mode}
                        data={user}
                        close={closeModal}
                        roles={roles}
                        sgg={sgg}
                        render={() => setRender((prevState) => !prevState)}
                    />
                ) : (
                    <ContentPasswordModal
                        mode={proc.mode}
                        data={user}
                        close={closeModal}
                        roles={roles}
                        sgg={sgg}
                        render={() => setRender((prevState) => !prevState)}
                    />
                )}
            </Modal>
        </div>
    );
}

export default User;
