package com.vityarthi.smartcampus.exception;

/**
 * Thrown when an enrollment would cause the student to exceed the maximum allowed credit limit per semester.
 */
public class CreditLimitExceededException extends CampusException {
    private final int currentCredits;
    private final int additionalCredits;
    private final int maxCredits;

    public CreditLimitExceededException(int currentCredits, int additionalCredits, int maxCredits) {
        super(String.format("Credit limit exceeded: Attempted to add %d credits to current %d credits (Maximum allowed: %d credits)",
                additionalCredits, currentCredits, maxCredits));
        this.currentCredits = currentCredits;
        this.additionalCredits = additionalCredits;
        this.maxCredits = maxCredits;
    }

    public int getCurrentCredits() {
        return currentCredits;
    }

    public int getAdditionalCredits() {
        return additionalCredits;
    }

    public int getMaxCredits() {
        return maxCredits;
    }
}
