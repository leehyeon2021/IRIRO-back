import React, { useState } from 'react';
import './App.css'; 

// ⭐ 1. 캐릭터 이미지 불러오기 (장바구니에서 꺼내기)
import myLocationImg from './assets/my_location_marker.png';

function App() {
  const [showDangerSpots, setShowDangerSpots] = useState(false);
  const [showSafeSpots, setShowSafeSpots] = useState(false);
  const [isModalOpen, setIsModalOpen] = useState(false);

  return (
    <div className="app-container">
      
      {/* 📍 가짜 지도 배경 */}
      <div style={{ position: 'absolute', top: 0, left: 0, width: '100%', height: '100%', zIndex: 10 }}>
        
        {/* ⭐ 2. 내 현재 위치 (파란 점 대신 캐릭터 등장!) */}
        <div className="my-location-wrapper">
          <div className="radar-pulse"></div> {/* 뒤에서 펄스 애니메이션 */}
          
          {/* 🚨 바로 이 줄이 빠져있어서 아까 경고가 뜬 거예요! (책상 위에 올리기) */}
          <img 
            src={myLocationImg} 
            alt="내 위치" 
            className="my-location-character" 
          />
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
        <button className="btn-menu">
          <div className="menu-bar"></div>
          <div className="menu-bar"></div>
          <div className="menu-bar"></div>
        </button>
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