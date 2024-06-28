import React, { useEffect, useState } from "react";
import { useLocation, Link, useNavigate } from "react-router-dom";

import Header from "./Header.js";
import Sidebar from "./Sidebar.js";

import Main from "../main/Main";
import ResearchMain from "../data/research/main/ResearchMain";
import ResearchShp from "../data/research/shp/ResearchShp";
import Report from "../data/research/report/Report";
import RschData from "../data/research/mng/RschData";
import FloorPlan from "../data/research/floorPlan/FloorPlan";

import GisPage from "../gis/GisPage";
import Analy from "../analy/Analy";
import User from "../system/user/User";
import Feedback from "../system/feedback/Feedback";

import Info from "../system/myPage/Info";
import Pw from "../system/myPage/Pw";

import PStatus from "../data/monthlyReport/read/status/PStatus.js";
import PIncrs from "../data/monthlyReport/read/subIncrs/PIncrs.js";
import PDcrs from "../data/monthlyReport/read/subDcrs/PDcrs.js";
import PPublic from "../data/monthlyReport/read/pub/PPublic.js";
import PResi from "../data/monthlyReport/read/resi/PResi.js";
import MrFile from "../data/monthlyReport/file/FileUpload.js";

import FileUpload from "../data/pf/file/FileUpload";
import PfPrivate from "../data/pf/read/private/PfPrivate";
// import PfPublic from "../data/pf/read/public/PfPublic";
// import PfOut from "../data/pf/read/out/pfOut";
import PfSub from "../data/pf/read/sub/PfSub";
import PfPrvOpen from "../data/pf/read/pvlOpen/PfPvOpen";
import PfSubOpen from "../data/pf/read/subOpen/PfSubOpen";
import PfStandardDataset from "../data/pf/read/standardDataset/PfStandardDataset";

import IllFileUpload from "../data/ill/file/FileUpload.js";
import BusMounted from "../data/ill/read/busMounted/BusMounted.js";
import Mobile from "../data/ill/read/mobile/Mobile.js";
import CrackDown from "../data/ill/read/crackDown/CrackDown.js";
import Fireplug from "../data/ill/read/fireplug/Fireplug.js";
import Fixed from "../data/ill/read/fixed/Fixed.js";
import Simungo from "../data/ill/read/simungo/Simungo.js";
import Protected from "../data/ill/read/protectedArea/ProtectedArea.js";
import Prfmnc from "../data/ill/prfmnc/Prfmnc";

function SetPage(props) {
    const pageHierarchy = {
        main: <Main />,
        rsch: {
            main: <ResearchMain />,
            shp: <ResearchShp />,
            report: <Report />,
            floorPlan: <FloorPlan />,
            mng: <RschData />,
        },
        data: {
            pf: {
                read: {
                    // public: <PfPublic />,
                    public: <PPublic />,
                    private: <PfPrivate />,
                    // out: <PfOut />,
                    sub: <PfSub />,
                    pvlOpen: <PfPrvOpen />,
                    subOpen: <PfSubOpen />,
                    standardDataset: <PfStandardDataset />,
                },
                file: <FileUpload />,
            },
            monthlyReport: {
                read: {
                    status: <PStatus />,
                    incrs: <PIncrs />,
                    dcrs: <PDcrs />,
                    public: <PPublic />,
                    resiFirst: <PResi />,
                },
                file: <MrFile />,
            },
            illegal: {
                read: {
                    mobile: <Mobile />,
                    busMounted: <BusMounted />,
                    crackDown: <CrackDown />,
                    fireplug: <Fireplug />,
                    fixed: <Fixed />,
                    sinmungo: <Simungo />,
                    protectedArea: <Protected />,
                },
                file: <IllFileUpload />,
                prfmnc: <Prfmnc />,
            },
        },
        gis: <GisPage />,
        analy: <Analy />,
        system: {
            user: <User />,
            feedback: <Feedback />,
            myPage: {
                info: <Info />,
                pw: <Pw />,
            },
        },
    };

    function findComponent(location, hierarchy) {
        const keys = location.split("/").filter(Boolean);

        for (const key of keys) {
            hierarchy = hierarchy[key];

            if (!hierarchy) {
                return null;
            }
        }

        if (React.isValidElement(hierarchy)) {
            return hierarchy;
        } else {
            return null;
        }
    }

    const pageComponent = findComponent(props.loc, pageHierarchy);

    if (pageComponent) {
        return pageComponent;
    } else {
        return null;
    }
}

function SetTitle(props) {
    const { tit } = props;

    const imgStyle = { width: "6.5em", marginBottom: ".25em" };

    return (
        <div className="page_info">
            <h2>{tit}</h2>
            {/* <div className="breadcrumbWrap">
                <img style={imgStyle} src={require("../../assets/img/common/img_title.png")} />
            </div> */}
        </div>
    );
}

function Layout(props) {
    const location = useLocation();
    const navigate = useNavigate();
    const { menu, tit } = props;

    const [sideOn, toggleSideOn] = useState(true);
    const [title, setTitle] = useState();
    const [subMenu, setSubMenu] = useState([]);
    const [tabMenu, setTabMenu] = useState([]);

    const [full, setFull] = useState(false);

    useEffect(() => {
        // sidebar
        menu.map((m) => {
            if (m.url === "/" + location.pathname.split("/")[1] && m.children !== 0) {
                setSubMenu(m.children);
                setTitle(m.name);

                if (m.children.length === 0) {
                    setFull(true);
                } else {
                    setFull(false);
                }
            }
        });

        const noneTree = [];

        (function createNoneTree(arr, menuList) {
            menuList.forEach((menu) => {
                arr.push(menu);

                if (menu.children) {
                    createNoneTree(arr, menu.children);
                }
            });
        })(noneTree, menu);

        let tabList = [];

        noneTree.forEach((ele) => {
            if (location.pathname == ele.url) {
                if (ele.tabYn == "Y") {
                    const parent = noneTree.find((ele2) => ele2.id == ele.parentId);

                    tabList = [...parent.children];
                } else {
                    tabList = [...ele.children.filter((ele2) => ele2.tabYn == "Y")];

                    if (tabList.length) {
                        const firstTab = tabList[0];
                        navigate(firstTab.url);
                    }
                }
            }
        });

        setTabMenu(tabList);
    }, [props]);

    return (
        <div className="wrapper">
            <Header menu={menu} toggle={toggleSideOn} on={sideOn} />
            {full ? (
                <></>
            ) : (
                <div className="menu_tree">
                    <div className="sidebar_top"></div>
                    <div className="container">
                        <Sidebar menu={subMenu} path={location.pathname} isShow={sideOn} title={title} />
                    </div>
                </div>
            )}

            <div className={full ? "" : "container"} id={full ? "" : "content_section"}>
                {tabMenu.length > 0 && (
                    <ul className="menu_tab">
                        {tabMenu.map((ele) => {
                            return (
                                <li
                                    key={ele.url}
                                    className={`${ele.url == location.pathname ? "on" : ""}`}
                                    onClick={() => {
                                        navigate(ele.url);
                                    }}
                                >
                                    {ele.name}
                                </li>
                            );
                        })}
                    </ul>
                )}

                <div className="page_content">
                    <SetPage loc={location.pathname} />
                </div>
            </div>

            {/* {location.pathname === "/main" ? <Footer /> : null} */}
        </div>
    );
}

export default Layout;
