from datetime import datetime
from typing import List

from fastapi import FastAPI
from pydantic import BaseModel, Field

app = FastAPI(title="Make To Order ML Service")


class ProductionSignal(BaseModel):
    line_id: str = Field(..., example="Line-A")
    utilization: float = Field(..., ge=0, le=100, example=78.5)
    defect_rate: float = Field(..., ge=0, le=100, example=1.8)
    backlog_hours: float = Field(..., ge=0, example=6.5)


class PredictionResponse(BaseModel):
    estimated_lead_time_days: float
    risk_level: str
    generated_at: datetime
    notes: List[str]


@app.get("/health")
def health_check():
    return {"status": "ok"}


@app.post("/predict/lead-time", response_model=PredictionResponse)
def predict_lead_time(signal: ProductionSignal):
    base = 2.5
    utilization_factor = signal.utilization / 100
    backlog_factor = min(signal.backlog_hours / 10, 1.5)
    defect_penalty = signal.defect_rate / 50

    estimated = base + (utilization_factor * 2.2) + backlog_factor + defect_penalty
    risk = "낮음"
    if estimated > 5:
        risk = "높음"
    elif estimated > 4:
        risk = "중간"

    notes = [
        f"라인 {signal.line_id} 기준 예측",
        f"가동률 {signal.utilization:.1f}% 반영",
        f"백로그 {signal.backlog_hours:.1f}h 반영",
    ]

    return PredictionResponse(
        estimated_lead_time_days=round(estimated, 2),
        risk_level=risk,
        generated_at=datetime.utcnow(),
        notes=notes,
    )
