## 1. 프로젝트 소개

### 1.1 프로젝트 이름: IRI-RO (이리로)

>**최단거리가 아닌 안전을 우선으로 하는 보행자 내비게이션 서비스**

### 1.2 기획 의도 및 배경
  - **사회적 측면**: 치안 인프라 정보 불균형 해소 및 서울 야간 보행자의 불안감 실질적 해소.
  - **기술적 측면**: 안전도 산출 자체 알고리즘 개발, AI 기반 뉴스 필터링 구현.
  - **데이터 측면**: 공공 안전 데이터 + 실시간 범죄 뉴스 + 시민 리뷰를 하나의 플랫폼에 통합.

## 2. 팀원 / 역할

- **강병모** :
  + 자체 안전 경로 추천 로직(Cost 알고리즘) 설계
  + Tmap POI 통합 검색 및 TmapAPI 연동
  + 지도 위 위험/안전 요소 마커 시각화 로직 구현

- **박소영** :
  + JWT 기반 보안 인증 시스템 및 회원 관리
  + 커뮤니티 기능 구현
  + 역지오코딩(위치-주소 변환) 구현

- **이태현** :
  + 공공데이터 API(안전시설물, 성범죄자 도로명 주소) 수집 및 가공(지오코딩 사용)
  + 치안/범죄 크롤링 시스템 구축
  + 기사게시판 기능 구현

## 3. 기술 스택

### **Backend**
<img src="https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white"> <img src="https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white"> <img src="https://img.shields.io/badge/Spring_Security-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white"> <img src="https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=mysql&logoColor=white">
<br>
<img src="https://img.shields.io/badge/Lombok-BC204B?style=for-the-badge&logo=lombok&logoColor=white"> <img src="https://img.shields.io/badge/Jackson-000000?style=for-the-badge&logo=json&logoColor=white"> <img src="https://img.shields.io/badge/Spring_Data_JPA-6DB33F?style=for-the-badge&logo=spring&logoColor=white">

### **Frontend**
<img src="https://img.shields.io/badge/React-61DAFB?style=for-the-badge&logo=react&logoColor=black"> <img src="https://img.shields.io/badge/HTML5-E34F26?style=for-the-badge&logo=html5&logoColor=white"> <img src="https://img.shields.io/badge/CSS3-1572B6?style=for-the-badge&logo=css3&logoColor=white"> <img src="https://img.shields.io/badge/JavaScript-F7DF1E?style=for-the-badge&logo=javascript&logoColor=black">

### **API**
<img src="https://img.shields.io/badge/Tmap_API-00C4FF?style=for-the-badge&logo=tmap&logoColor=white"> <img src="https://img.shields.io/badge/Public_Data_API-003399?style=for-the-badge&logo=datagov&logoColor=white"> <img src="https://img.shields.io/badge/Kakao_API-FFCD00?style=for-the-badge&logo=kakao&logoColor=black"> <img src="https://img.shields.io/badge/Cloudinary-3448C5?style=for-the-badge&logo=cloudinary&logoColor=white">

### **ETC**
<img src="https://img.shields.io/badge/GitHub-181717?style=for-the-badge&logo=github&logoColor=white"> <img src="https://img.shields.io/badge/Figma-F24E1E?style=for-the-badge&logo=figma&logoColor=white"> <img src="https://img.shields.io/badge/Draw.io-F08705?style=for-the-badge&logo=diagramsdotnet&logoColor=white"> <img src="https://img.shields.io/badge/Jira/Confluence-0052CC?style=for-the-badge&logo=atlassian&logoColor=white">
<br>
<img src="https://img.shields.io/badge/AI_Filtering-FF6C37?style=for-the-badge&logo=openai&logoColor=white"> <img src="https://img.shields.io/badge/Ollama-000000?style=for-the-badge&logo=ollama&logoColor=white"> <img src="https://img.shields.io/badge/Cloudflare-F38020?style=for-the-badge&logo=cloudflare&logoColor=white">

## 4. 서비스 아키텍처
![아키텍처 이미지](이미지경로)

## 5. 시연영상 링크
[<시연영상 하이퍼링크/Youtube>](https://youtu.be/AKQrR80x6Bg)

## 6. 참고 링크

### SPRING GIT REPOSITORY(Back):<https://github.com/leehyeon2021/IRIRO-back>
### REACT GIT REPOSITORY(Front):<https://github.com/leehyeon2021/IRIRO-front>
### API 명세서: <여기다 구글시트 링크를 넣으면 되나요?>
### 디자인(Figma):<https://www.figma.com/design/y6Z3AUUDNcN1ZgDiusn0p6/%EC%A0%95%EA%B7%9C-4%EC%A1%B0-%ED%99%94%EC%9D%B4%ED%8C%85---?node-id=4-3&t=9PDtrDXxyKiTCI7n-0>
### ppt:<https://www.canva.com/design/DAHGs3uwaoI/xmaIf_GOdktfPgK6yi6dkw/edit>

