import { useEffect, useRef } from "react";

export default function MapPage() {
  const mapRef = useRef(null);

  useEffect(() => {
    if (!window.Tmapv2 || !mapRef.current) return;

    new window.Tmapv2.Map(mapRef.current, {
      center: new window.Tmapv2.LatLng(37.38953, 126.9594),
      width: "100%",
      height: "500px",
      zoom: 16,
      zoomControl: true,
      scrollwheel: true,
    });
  }, []);

  return (
    <div>
      <h2>지도 테스트</h2>
      <div ref={mapRef} style={{ width: "100%", height: "500px" }} />
    </div>
  );
}