import { useEffect, useMemo, useState } from "react";

const kpiData = [
  {
    label: "오늘 가동률",
    value: "92.4%",
    change: "+4.2%",
    note: "예상 대비"
  },
  {
    label: "예약 대기",
    value: "128건",
    change: "-12건",
    note: "전일 대비"
  },
  {
    label: "긴급 주문",
    value: "24건",
    change: "+3건",
    note: "실시간"
  },
  {
    label: "AI 리드타임",
    value: "3.8일",
    change: "±0.4일",
    note: "예측 오차"
  }
];

const fallbackStores = [
  {
    id: 1,
    name: "해저기지 서바이벌 스토어",
    description: "산소, 식량, 정수 등 생존 필수품 전문",
    zone: "Alpha Deck",
    tags: ["생존", "필수 보급", "24H"]
  },
  {
    id: 2,
    name: "심해 거주 모듈 공방",
    description: "외벽 패널과 압력 밸브를 담당하는 방어 라인",
    zone: "Habitat Core",
    tags: ["내압", "외벽", "모듈"]
  },
  {
    id: 3,
    name: "심해 탐사 장비소",
    description: "잠수복/드론 등 고급 탐사 장비 라인",
    zone: "Explorer Bay",
    tags: ["탐사", "드론", "고급"]
  }
];

const fallbackProducts = {
  1: [
    { id: 11, name: "산소 캡슐 정수 필터", tier: "ESSENTIAL", stock: 120, price: 180000, category: "생존 필수품" },
    { id: 12, name: "물 팩 리필 키트", tier: "ESSENTIAL", stock: 160, price: 68000, category: "생존 필수품" },
    { id: 13, name: "식량 팩 30일 세트", tier: "ESSENTIAL", stock: 90, price: 140000, category: "생존 필수품" }
  ],
  2: [
    { id: 21, name: "외벽 패널 X7", tier: "ADVANCED", stock: 40, price: 780000, category: "구조 강화" },
    { id: 22, name: "압력 밸브 듀얼 코어", tier: "ADVANCED", stock: 55, price: 560000, category: "구조 강화" },
    { id: 23, name: "내압 강화 프레임", tier: "ADVANCED", stock: 35, price: 620000, category: "구조 강화" }
  ],
  3: [
    { id: 31, name: "잠수복 부품 세트 S-12", tier: "ADVANCED", stock: 28, price: 880000, category: "탐사 장비" },
    { id: 32, name: "드론 부품 키트 D-4", tier: "ADVANCED", stock: 65, price: 420000, category: "탐사 장비" },
    { id: 33, name: "소형 수중 드론 모듈", tier: "ADVANCED", stock: 48, price: 540000, category: "탐사 장비" }
  ]
};

const timeline = [
  {
    title: "생존품 예약 접수",
    desc: "상점에서 생존 필수품을 예약하면 AI가 해저기지 부하를 분석합니다.",
    badge: "생존"
  },
  {
    title: "라인별 공급 조정",
    desc: "필수품·고급 부품 수요를 분석해 최적 생산 슬롯을 배정합니다.",
    badge: "공급"
  },
  {
    title: "주문·결제 프로토콜",
    desc: "해저기지 계약과 결제 기록을 통합해 납기 흐름을 단순화합니다.",
    badge: "주문"
  },
  {
    title: "심해 AI 예측",
    desc: "설비 데이터를 학습해 리드타임과 위험도를 실시간으로 예측합니다.",
    badge: "AI"
  }
];

const flowCards = [
  {
    title: "해저기지 예약 주문",
    desc: "생존 물자와 고급 부품을 예약 기반으로 확보하고 주문으로 확정합니다.",
    meta: ["예약승인", "주문확정", "납기알림"]
  },
  {
    title: "생존 필수품 라인",
    desc: "산소·정수·식량 등 필수품 공급을 우선 순위로 조정합니다.",
    meta: ["필수품", "재고", "보급"]
  },
  {
    title: "고급 부품 생산",
    desc: "잠수복·드론·외벽 패널 등 고급 라인을 전담 관리합니다.",
    meta: ["고급라인", "심해장비", "모듈"]
  },
  {
    title: "AI 리드타임 모델",
    desc: "Python 서비스가 심해 설비 데이터를 학습해 납기를 예측합니다.",
    meta: ["회귀모델", "예측", "파이프라인"]
  }
];

const fallbackReservations = [
  {
    name: "LG 스마트 패널",
    date: "2026-01-24 10:00",
    line: "Line-A",
    status: "확정"
  },
  {
    name: "Alpha 센서 모듈",
    date: "2026-01-24 13:30",
    line: "Line-B",
    status: "대기"
  },
  {
    name: "Bespoke Housing",
    date: "2026-01-25 09:00",
    line: "Line-C",
    status: "확정"
  }
];

const fallbackPredictions = [
  {
    label: "라인 B 이상 탐지",
    value: "위험 중간",
    note: "진동 상승 감지"
  },
  {
    label: "다음 7일 수요",
    value: "+12%",
    note: "온라인 캠페인 영향"
  },
  {
    label: "유지보수 권장",
    value: "2일 내",
    note: "열화 패턴"
  }
];

const formatDateTime = (value) => {
  if (!value) return "-";
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return value;
  return date.toLocaleString("ko-KR", { hour12: false });
};

const formatCredits = (value) => {
  if (value === null || value === undefined) return "-";
  const numberValue = typeof value === "number" ? value : Number(value);
  if (Number.isNaN(numberValue)) return value;
  return `${numberValue.toLocaleString("ko-KR")}`;
};

export default function App() {
  const [reservations, setReservations] = useState(fallbackReservations);
  const [predictions, setPredictions] = useState(fallbackPredictions);
  const [apiStatus, setApiStatus] = useState("idle");
  const [stores, setStores] = useState(fallbackStores);
  const [selectedStoreId, setSelectedStoreId] = useState(fallbackStores[0].id);
  const [products, setProducts] = useState(fallbackProducts[fallbackStores[0].id]);
  const [orderStoreId, setOrderStoreId] = useState(fallbackStores[0].id);
  const [orderRequester, setOrderRequester] = useState("Alpha Station");
  const [orderUrgency, setOrderUrgency] = useState("즉시");
  const [orderMemo, setOrderMemo] = useState("");
  const [orderItem, setOrderItem] = useState(
    fallbackProducts[fallbackStores[0].id]?.[0]?.name ?? ""
  );
  const [catalogStatus, setCatalogStatus] = useState("idle");

  const apiBase = useMemo(
    () => import.meta.env.VITE_API_BASE ?? "http://localhost:8080",
    []
  );

  useEffect(() => {
    let isMounted = true;
    const fetchData = async () => {
      try {
        setApiStatus("loading");
        const [reservationRes, mlRes] = await Promise.all([
          fetch(`${apiBase}/api/reservations`),
          fetch(`${apiBase}/api/ml/lead-time`)
        ]);

        if (reservationRes.ok) {
          const data = await reservationRes.json();
          if (isMounted && Array.isArray(data) && data.length > 0) {
            setReservations(
              data.map((item) => ({
                name: `${item.customerName} · ${item.productName}`,
                date: formatDateTime(item.reservedAt),
                line: item.lineId,
                status: item.status === "CONFIRMED" ? "확정" : "대기"
              }))
            );
          }
        }

        if (mlRes.ok) {
          const result = await mlRes.json();
          if (isMounted && result) {
            setPredictions([
              {
                label: "AI 리드타임",
                value: `${result.estimatedLeadTimeDays}일`,
                note: `리스크 ${result.riskLevel}`
              },
              {
                label: "모델 생성 시간",
                value: formatDateTime(result.generatedAt),
                note: "UTC 기준"
              },
              {
                label: "모델 메모",
                value: result.notes?.[0] ?? "데이터 수집 중",
                note: result.notes?.[1] ?? "라인 상태 확인"
              }
            ]);
          }
        }

        if (isMounted) {
          setApiStatus("ready");
        }
      } catch (error) {
        if (isMounted) {
          setApiStatus("fallback");
        }
      }
    };

    fetchData();
    return () => {
      isMounted = false;
    };
  }, [apiBase]);

  const setActiveStore = (storeId, options = { updateOrderStore: true }) => {
    setSelectedStoreId(storeId);
    if (options.updateOrderStore) {
      setOrderStoreId(storeId);
    }
    const fallbackName = fallbackProducts[storeId]?.[0]?.name ?? "";
    setOrderItem(fallbackName);
  };

  useEffect(() => {
    let isMounted = true;
    const fetchStores = async () => {
      try {
        setCatalogStatus("loading");
        const response = await fetch(`${apiBase}/api/stores`);
        if (response.ok) {
          const data = await response.json();
          if (isMounted && Array.isArray(data) && data.length > 0) {
            setStores(data);
            setActiveStore(data[0].id);
          }
        }
        if (isMounted) {
          setCatalogStatus("ready");
        }
      } catch (error) {
        if (isMounted) {
          setCatalogStatus("fallback");
        }
      }
    };

    fetchStores();
    return () => {
      isMounted = false;
    };
  }, [apiBase]);

  useEffect(() => {
    let isMounted = true;
    const fetchProducts = async () => {
      if (!selectedStoreId) return;
      try {
        const response = await fetch(`${apiBase}/api/stores/${selectedStoreId}/products`);
        if (response.ok) {
          const data = await response.json();
          if (isMounted && Array.isArray(data) && data.length > 0) {
            setProducts(data);
            return;
          }
        }
        if (isMounted) {
          setProducts(fallbackProducts[selectedStoreId] ?? []);
        }
      } catch (error) {
        if (isMounted) {
          setProducts(fallbackProducts[selectedStoreId] ?? []);
        }
      }
    };

    fetchProducts();
    return () => {
      isMounted = false;
    };
  }, [apiBase, selectedStoreId]);

  useEffect(() => {
    const fallbackName = fallbackProducts[selectedStoreId]?.[0]?.name ?? "";
    const firstProduct = products[0]?.name;
    setOrderItem(firstProduct ?? fallbackName);
  }, [products, selectedStoreId]);

  return (
    <div className="app">
      <header className="hero">
        <nav className="nav">
          <div className="brand">
            <span className="brand__pulse" />
            Make To Order Factory
          </div>
          <div className="nav__links">
            <button className="ghost">예약 흐름</button>
            <button className="ghost">대시보드</button>
            <button className="ghost">AI 예측</button>
            <button className="cta">데모 요청</button>
          </div>
        </nav>

        <div className="hero__grid">
          <div className="hero__content">
            <p className="eyebrow">Underwater Base Integrated Operations</p>
            <h1>
              해저기지 통합 솔루션으로
              <span>생존·생산·공급을 한 번에</span>
            </h1>
            <p className="subtitle">
              해저기지 상점 네트워크, 예약 기반 주문, 고급 부품 생산을 하나의
              대시보드로 연결하고 AI 예측으로 리드타임과 위험도를 관리합니다.
            </p>
            <div className="hero__actions">
              <button className="cta">예약 시뮬레이션</button>
              <button className="secondary">설계 문서 보기</button>
            </div>
            <div className="hero__chips">
              <span>React + Vite</span>
              <span>Spring Boot</span>
              <span>Python ML</span>
              <span>Docker Compose</span>
            </div>
          </div>

          <div className="hero__panel">
            <div className="panel__header">
              <div>
                <p className="panel__title">스마트 팩토리 현황</p>
                <p className="panel__desc">실시간 생산 모니터링 & 예측</p>
              </div>
              <span className="status">LIVE</span>
            </div>
            <div className="kpi-grid">
              {kpiData.map((item) => (
                <div className="kpi" key={item.label}>
                  <p className="kpi__label">{item.label}</p>
                  <p className="kpi__value">{item.value}</p>
                  <p className="kpi__meta">{item.change} · {item.note}</p>
                </div>
              ))}
            </div>
            <div className="panel__footer">
              <div>
                <p className="footer__label">다음 생산 슬롯</p>
                <p className="footer__value">01:45:12 남음</p>
              </div>
              <button className="ghost">슬롯 관리</button>
            </div>
          </div>
        </div>
      </header>

      <main>
        <section className="section">
          <div className="section__title">
            <h2>통합 설계 흐름</h2>
            <p>예약 → 주문 → 생산 → AI 예측까지 하나의 파이프라인으로 관리합니다.</p>
          </div>
          <div className="timeline">
            {timeline.map((item, index) => (
              <article className="timeline__card" key={item.title}>
                <div className="timeline__badge">0{index + 1}</div>
                <div>
                  <span className="chip">{item.badge}</span>
                  <h3>{item.title}</h3>
                  <p>{item.desc}</p>
                </div>
              </article>
            ))}
          </div>
        </section>

        <section className="section catalog">
          <div className="section__title">
            <h2>해저기지 상점 네트워크</h2>
            <p>상점을 선택하면 해당 라인의 생존 필수품·고급 부품을 확인할 수 있습니다.</p>
          </div>
          <div className="catalog__grid">
            <div className="store-list">
              {stores.map((store) => (
                <button
                  key={store.id}
                  className={`store-card ${selectedStoreId === store.id ? "store-card--active" : ""}`}
                  onClick={() => setActiveStore(store.id)}
                >
                  <div>
                    <p className="store-card__zone">{store.zone}</p>
                    <h3>{store.name}</h3>
                    <p>{store.description}</p>
                    <div className="store-card__tags">
                      {(store.tags ?? []).map((tag) => (
                        <span key={tag}>{tag}</span>
                      ))}
                    </div>
                  </div>
                  <span className="store-card__status">
                    {catalogStatus === "loading" ? "SYNC" : "READY"}
                  </span>
                </button>
              ))}
            </div>
            <div className="product-panel">
              <div className="product-panel__header">
                <div>
                  <h3>라인별 품목</h3>
                  <p>선택된 상점의 재고를 표시합니다.</p>
                </div>
                <span className="chip">{products.length} items</span>
              </div>
              <div className="product-list">
                {products.map((product) => (
                  <div className="product" key={product.id ?? product.name}>
                    <div>
                      <p className="product__name">{product.name}</p>
                      <p className="product__meta">
                        {product.category ?? (product.tier === "ADVANCED" ? "고급 라인" : "필수 생존")}
                      </p>
                    </div>
                    <div className="product__stock">
                      <span>재고</span>
                      <strong>{product.stock}</strong>
                      <p className="product__price">{formatCredits(product.price)} 크레딧</p>
                    </div>
                  </div>
                ))}
              </div>
            </div>
          </div>
        </section>

        <section className="section glass">
          <div className="section__title">
            <h2>모듈 구성</h2>
            <p>스마트 팩토리 중심의 커머스 운영 흐름을 구성했습니다.</p>
          </div>
          <div className="grid">
            {flowCards.map((card) => (
              <article className="card" key={card.title}>
                <div className="card__header">
                  <h3>{card.title}</h3>
                  <span className="signal"></span>
                </div>
                <p>{card.desc}</p>
                <div className="meta">
                  {card.meta.map((item) => (
                    <span key={item}>{item}</span>
                  ))}
                </div>
              </article>
            ))}
          </div>
        </section>

        <section className="section split">
          <div>
            <div className="section__title">
              <h2>해저기지 보급 예약 현황</h2>
              <p>생존 물자와 고급 부품 요청을 우선순위로 정렬해 생산 슬롯을 배정합니다.</p>
            </div>
            <div className="table">
              <div className="table__row table__header">
                <span>고객/제품</span>
                <span>예약 시간</span>
                <span>생산 라인</span>
                <span>상태</span>
              </div>
              {reservations.map((row) => (
                <div className="table__row" key={row.name}>
                  <span>{row.name}</span>
                  <span>{row.date}</span>
                  <span>{row.line}</span>
                  <span className={`status-pill status-pill--${row.status === "확정" ? "ready" : "wait"}`}>
                    {row.status}
                  </span>
                </div>
              ))}
            </div>
          </div>

          <div className="panel">
            <h3>AI 예측 센터</h3>
            <p className="panel__desc">Python ML 서비스가 생산 데이터를 학습합니다.</p>
            <div className="panel__stack">
              {predictions.map((item) => (
                <div className="panel__item" key={item.label}>
                  <div>
                    <p className="panel__label">{item.label}</p>
                    <p className="panel__value">{item.value}</p>
                  </div>
                  <p className="panel__note">{item.note}</p>
                </div>
              ))}
            </div>
            <button className="cta">모델 상세 보기</button>
          </div>
        </section>

        <section className="section order">
          <div className="section__title">
            <h2>긴급 보급 주문 요청</h2>
            <p>선택된 상점과 품목으로 즉시 주문 요청을 전송해 생산 라인을 확보합니다.</p>
          </div>
          <div className="order-grid">
            <div className="order-form">
              <label>
                요청자
                <input
                  type="text"
                  placeholder="예: Alpha Station"
                  value={orderRequester}
                  onChange={(e) => setOrderRequester(e.target.value)}
                />
              </label>
              <label>
                상점 선택
                <select
                  value={orderStoreId}
                  onChange={(e) => setActiveStore(Number(e.target.value))}
                >
                  {stores.map((store) => (
                    <option value={store.id} key={store.id}>
                      {store.name}
                    </option>
                  ))}
                </select>
              </label>
              <label>
                주문 품목
                <input
                  type="text"
                  placeholder="예: 산소 캡슐 정수 필터"
                  value={orderItem}
                  onChange={(e) => setOrderItem(e.target.value)}
                />
              </label>
              <label>
                긴급도
                <select value={orderUrgency} onChange={(e) => setOrderUrgency(e.target.value)}>
                  <option value="즉시">즉시</option>
                  <option value="24시간">24시간</option>
                  <option value="정상">정상</option>
                </select>
              </label>
              <label className="order-form__wide">
                요청 메모
                <textarea
                  rows="3"
                  placeholder="특이사항 또는 납기 조건"
                  value={orderMemo}
                  onChange={(e) => setOrderMemo(e.target.value)}
                />
              </label>
              <div className="order-actions">
                <button className="secondary">임시 저장</button>
                <button className="cta">주문 요청</button>
              </div>
            </div>
            <div className="order-hint">
              <h3>주문 승인 규칙</h3>
              <ul>
                <li>생존 필수품은 긴급도 우선 배정</li>
                <li>고급 부품은 재고와 가동률 기준 스케줄링</li>
                <li>AI 예측 리드타임과 충돌 시 자동 알림</li>
              </ul>
            </div>
          </div>
        </section>
      </main>

      <footer className="footer">
        <div>
          <h2>Make To Order Factory</h2>
          <p>예약 기반 제조·주문 시스템 설계 포트폴리오</p>
        </div>
        <div className="footer__links">
          <span>Version 0.1</span>
          <span>Last updated 2026.01</span>
        </div>
      </footer>
    </div>
  );
}
