package ru.pulsecore.app.core.dto;

import java.time.LocalDate;
import java.util.List;

public class StatProjection {
    private String label;
    private double sum;
    private long count;
    private double avg;
    private LocalDate start;
    private LocalDate end;
    private List<StatProjection> children;

    public StatProjection() {}

    public StatProjection(String label, double sum, long count, LocalDate start, LocalDate end) {
        this.label = label;
        this.sum = sum;
        this.count = count;
        this.avg = count > 0 ? sum / count : 0;
        this.start = start;
        this.end = end;
    }

    public String getLabel() { return label; }
    public double getSum() { return sum; }
    public long getCount() { return count; }
    public double getAvg() { return avg; }
    public LocalDate getStart() { return start; }
    public LocalDate getEnd() { return end; }
    public List<StatProjection> getChildren() { return children; }

    public void setLabel(String label) { this.label = label; }
    public void setSum(double sum) { this.sum = sum; }
    public void setCount(long count) { this.count = count; }
    public void setAvg(double avg) { this.avg = avg; }
    public void setStart(LocalDate start) { this.start = start; }
    public void setEnd(LocalDate end) { this.end = end; }
    public void setChildren(List<StatProjection> children) { this.children = children; }

    public String toContextString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%s: %.0f руб, %d турниров (средний чек: %.0f руб)\n",
                label, sum, count, avg));
        if (children != null && !children.isEmpty()) {
            for (StatProjection child : children) {
                sb.append("  ").append(child.toContextString());
            }
        }
        return sb.toString();
    }
}