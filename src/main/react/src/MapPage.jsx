import { useEffect, useRef } from "react";

export default function MapPage() {
  const mapRef = useRef(null);

  useEffect(() => {
    if (!window.Tmapv2 || !window.Tmapv2.LatLng || !mapRef.current) return;

    new window.Tmapv2.Map(mapRef.current, {
      center: new window.Tmapv2.LatLng(37.38953, 126.9594),
      width: "100%",
      height: "100%",
      zoom: 16,
      zoomControl: true,
      scrollwheel: true,
    });
  }, []);

  return (
    <div
      ref={mapRef}
      style={{
        position: "absolute",
        top: 0,
        left: 0,
        width: "100%",
        height: "100%",
        zIndex: 1,
      }}
    />
  );
}