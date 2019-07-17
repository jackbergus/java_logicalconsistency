package org.ufl.hypogator.jackb.fuzzymatching.ldc;

public class LDCResult {
    public String kbId;
    public String resolved;
    public double score;
    public String nistType;

    public LDCResult(String resolved) {
        this.kbId = null;
        this.nistType = null;
        this.resolved = resolved.trim();
        this.score = 1.0;
    }

    public LDCResult(String kbId, String resolved, double score, String nistType) {
        this.kbId = kbId;
        this.resolved = resolved.trim();
        this.score = score;
        this.nistType = nistType;
    }

    public LDCResult copyExcept(double newScore) {
        return new LDCResult(kbId, resolved, newScore, nistType);
    }

    public LDCResult copyExceptResolved(String newResolved) {
        return new LDCResult(kbId, newResolved.trim(), score, nistType);
    }

    public double score() {
        return score;
    }

    @Override
    public String toString() {
        return "LDCResult{" +
                "kbId='" + kbId + '\'' +
                ", resolved='" + resolved + '\'' +
                ", score=" + score +
                ", nistType='" + nistType + '\'' +
                '}';
    }
}
