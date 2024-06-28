import React from "react";

function Footer(props) {
    return (
        <footer className="footer">
            <div className="container">
                <div className="footer_logo">
                    <img src={require("../../assets/img/common/footer_logo.png")} />
                </div>
                <div className="footer_info">
                    <div className="footer_addr">
                        <span>(44675) 울산광역시 남구 중앙로 201 (신정동)</span>
                        <span>대표전화 : 052-120</span>
                    </div>

                    <div className="footer_copyright">Ulsan Metropolitan City all rights reserved</div>
                </div>
            </div>
        </footer>
    );
}

export default Footer;
