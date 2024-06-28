import React, { useEffect, useRef, useState } from "react";
import Draggable from "react-draggable";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faMinus, faPlus } from "@fortawesome/free-solid-svg-icons";
import ContentLayerMenu from "./ContentLayerMenu";

function ContentMenu({ SideComponent, title, mode, type, toggleButton, setToggleButton }) {
    const menuRef = useRef();

    return (
        <>
            <div className={`mapContent static ${type == "layer" ? "layer" : "card"}`}>
                <ul className="mapContentTab">
                    <li>
                        {title}
                        <FontAwesomeIcon
                            icon={toggleButton ? faMinus : faPlus}
                            color="white"
                            style={{ fontWeight: "bold", fontSize: "15px" }}
                            onClick={() => {
                                setToggleButton((prevState) => !prevState);
                            }}
                        />
                    </li>
                </ul>
                <div className="mapContBody" style={{ display: `${toggleButton ? "flex" : "none"}` }}>
                    {SideComponent}
                </div>
            </div>
        </>
    );
}

export default ContentMenu;
