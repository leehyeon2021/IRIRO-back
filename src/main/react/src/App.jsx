import React, { useEffect, useRef, useState } from 'react';
import './App.css';
import myLocationImg from './assets/my_location_marker.png';

function App() {
  const [showDangerSpots, setShowDangerSpots] = useState(false);
  const [showSafeSpots, setShowSafeSpots] = useState(false);
  const [isModalOpen, setIsModalOpen] = useState(false);
  // ⭐ 1단계: 왼쪽 아래 메뉴를 열고 닫을 스위치 추가!
  const [isMenuOpen, setIsMenuOpen] = useState(false);

  const mapRef = useRef(null);

  useEffect(() => {
      if (!window.Tmapv2 || !mapRef.current) return;

      new window.Tmapv2.Map(mapRef.current, {
        center: new window.Tmapv2.LatLng(37.38953, 126.9594),
        width: '100%',
        height: '100%',
        zoom: 16,
        zoomControl: true,
        scrollwheel: true
      });
    }, []);

  return (
    <div className="app-container">

      {/* 진짜 지도 */}
      <div
        ref={mapRef}
        style={{
          position: 'absolute',
          top: 0,
          left: 0,
          width: '100%',
          height: '100%',
          zIndex: 10
        }}
      />
      {/* 지도 위에 덮는 커스텀 UI */}
          <div
            style={{
              position: 'absolute',
              top: 0,
              left: 0,
              width: '100%',
              height: '100%',
              zIndex: 10,
              pointerEvents: 'none'
            }}
          >

        {/* 기존 코드 */}
        <div className="my-location-wrapper">
          <div className="radar-pulse"></div>
          <img src={myLocationImg} alt="내 위치" className="my-location-character" />
        </div>

        <div className="marker-warning" style={{ top: '400px', left: '250px' }}></div>

        {showDangerSpots && (
          <>
            <div className="marker-danger" style={{ top: '250px', left: '150px' }}></div>
            <div className="marker-danger" style={{ top: '300px', left: '220px' }}></div>
            <div className="marker-danger" style={{ top: '65%', left: '35%' }}></div>
            <div className="marker-danger" style={{ top: '55%', left: '25%' }}></div>
          </>
        )}

        {showSafeSpots && (
          <>
            <div className="marker-safe" style={{ top: '150px', left: '280px' }}></div>
            <div className="marker-safe" style={{ top: '450px', left: '100px' }}></div>
            <div className="marker-safe" style={{ top: '75%', left: '60%' }}></div>
          </>
        )}
      </div>

      {/* ⬆️ 상단 영역 */}
      <div className="top-wrapper">
        <div className="search-bar">
          <span className="logo">이리로</span>
          <span className="search-text">안전 경로 탐색</span>
          <span className="search-icon">🔍</span>
        </div>
        <div className="filter-buttons">
          <button className="btn-filter btn-danger" onClick={() => setShowDangerSpots(!showDangerSpots)}>⚠️ 위험 구역</button>
          <button className="btn-filter btn-safe" onClick={() => setShowSafeSpots(!showSafeSpots)}>✅ 안전 구역</button>
        </div>
      </div>

      {/* ⬇️ 하단 영역 */}
      <div className="bottom-wrapper">

        {/* ⭐ 2단계: 왼쪽 메뉴 버튼 그룹 (버튼과 팝업을 하나로 묶음) */}
        <div style={{ position: 'relative' }}>

          {/* 스위치(isMenuOpen)가 켜졌을 때만 팝업 메뉴 보이기 */}
          {isMenuOpen && (
            <div className="menu-popup">
              <button
                className="btn-menu-item"
                onClick={() => {
                  // 나중에 여기에 진짜 커뮤니티 페이지로 넘어가는 코드를 넣으면 됩니다!
                  alert("커뮤니티 페이지로 이동합니다! 📢");
                  setIsMenuOpen(false); // 클릭 후에는 메뉴 다시 닫아주기
                }}
              >
                📢
              </button>
            </div>
          )}

          {/* 기존 햄버거 메뉴 버튼 (누를 때마다 스위치 껐다 켜기) */}
          <button className="btn-menu" onClick={() => setIsMenuOpen(!isMenuOpen)}>
            <div className="menu-bar"></div>
            <div className="menu-bar"></div>
            <div className="menu-bar"></div>
          </button>
        </div>

        {/* 오른쪽 신고 버튼 */}
        <button className="btn-menu btn-report" onClick={() => setIsModalOpen(true)}>
          <span>🚨</span>
          <span>신고</span>
        </button>
      </div>

      {/* 모달창 조건부 렌더링 */}
      {isModalOpen && (
        <div className="modal-overlay">
          <div className="modal-content">
            <h2 className="modal-title">🚨 긴급 신고</h2>
            <p className="modal-text">현재 위치를 기반으로<br/>경찰에 긴급 신고하시겠습니까?</p>
            <div className="modal-buttons">
              <button className="btn-modal btn-cancel" onClick={() => setIsModalOpen(false)}>취소</button>
              <button className="btn-modal btn-confirm" onClick={() => {
                alert("신고가 접수되었습니다!");
                setIsModalOpen(false);
              }}>신고하기</button>
            </div>
          </div>
        </div>
      )}

    </div>
  );
}

export default App;