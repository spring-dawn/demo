import { OSM, XYZ, TileWMS, Vector as VectorSource } from "ol/source"; //지도정보
import { View } from "ol";
import { get as getProj } from "ol/proj";
import { getCenter } from "ol/extent";

const MAP_EXTENT = [14300814.050062528, 4202457.511417287, 14477307.148372998, 4270715.77767595];

let mapOption = {
    mapBgList: {
        // map background
        vBase: {
            source: new XYZ({
                url: "http://api.vworld.kr/req/wmts/1.0.0/BDF41B1E-F857-3FC6-A201-FDF4D19B1F16/Base/{z}/{y}/{x}.png",
            }),
            name: "기본지도",
            Image: require("../../assets/img/gis/map/default_map.png"),
        },
        vSAT: {
            source: new XYZ({
                url: "http://api.vworld.kr/req/wmts/1.0.0/BDF41B1E-F857-3FC6-A201-FDF4D19B1F16/Satellite/{z}/{y}/{x}.jpeg",
            }),
            name: "위성지도",
            Image: require("../../assets/img/gis/map/sate_map.png"),
        },
        vWhite: {
            source: new XYZ({
                url: "http://api.vworld.kr/req/wmts/1.0.0/BDF41B1E-F857-3FC6-A201-FDF4D19B1F16/white/{z}/{y}/{x}.png",
            }),
            name: "백지도",
            Image: require("../../assets/img/gis/map/white_map.png"),
        },
        vMidn: {
            source: new XYZ({
                url: "http://api.vworld.kr/req/wmts/1.0.0/BDF41B1E-F857-3FC6-A201-FDF4D19B1F16/midnight/{z}/{y}/{x}.png",
            }),
            name: "야간지도",
            Image: require("../../assets/img/gis/map/night_map.png"),
        },
        OSM: {
            source: new OSM(),
            name: "OS지도",
            Image: require("../../assets/img/gis/map/OS_map.png"),
        },
    },
    mapBaseList: {},
    view: new View({
        projection: getProj("EPSG:3857"),
        extent: MAP_EXTENT,
        minZoom: 11,
        maxZoom: 19,
        center: getCenter(MAP_EXTENT),
        zoom: 11,
    }),
};

export default mapOption;
