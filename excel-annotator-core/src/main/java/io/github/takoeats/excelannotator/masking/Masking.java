package io.github.takoeats.excelannotator.masking;

/**
 * Excel 컬럼 데이터 마스킹 프리셋
 * <p>민감한 개인정보, 금융정보 등을 마스킹 처리하여 엑셀 내보내기 시 보안을 강화합니다.</p>
 * <p>커스텀 마스킹이 필요한 경우, DTO에서 데이터를 넣기 전에 직접 마스킹 처리하시기 바랍니다.</p>
 *
 * <h3>사용 예시</h3>
 * <pre>{@code
 * @ExcelColumn(header = "전화번호", masking = Masking.PHONE)
 * private String phoneNumber;
 *
 * @ExcelColumn(header = "이메일", masking = Masking.EMAIL)
 * private String email;
 * }</pre>
 */
public enum Masking {

    /**
     * 마스킹 없음 (기본값)
     */
    NONE {
        @Override
        public String mask(String value) {
            return value;
        }
    },

    /**
     * 전화번호 마스킹
     * <p>예시: 010-1234-5678 → 010-****-5678</p>
     * <p>예시: 02-1234-5678 → 02-****-5678</p>
     */
    PHONE {
        @Override
        public String mask(String value) {
            if (isNullOrEmpty(value)) return value;

            String digits = value.replaceAll("[^0-9]", "");

            if (digits.length() == 11) {
                return String.format("%s-****-%s",
                        digits.substring(0, 3),
                        digits.substring(7));
            } else if (digits.length() == 10) {
                return String.format("%s-****-%s",
                        digits.substring(0, 2),
                        digits.substring(6));
            } else if (digits.length() == 9) {
                return String.format("%s-***-%s",
                        digits.substring(0, 2),
                        digits.substring(5));
            }

            return MaskingUtil.maskMiddle(value, 3, 4);
        }
    },

    /**
     * 이메일 마스킹
     * <p>예시: user@example.com → u***@example.com</p>
     * <p>예시: a@test.com → a***@test.com</p>
     */
    EMAIL {
        @Override
        public String mask(String value) {
            if (isNullOrEmpty(value)) return value;

            int atIndex = value.indexOf('@');
            if (atIndex <= 0) return value;

            String local = value.substring(0, atIndex);
            String domain = value.substring(atIndex);

            if (local.length() <= 1) {
                return local + "***" + domain;
            }

            return local.charAt(0) + "***" + domain;
        }
    },

    /**
     * 주민등록번호 마스킹
     * <p>예시: 123456-1234567 → 123456-*******</p>
     * <p>예시: 1234561234567 → 123456*******</p>
     */
    SSN {
        @Override
        public String mask(String value) {
            if (isNullOrEmpty(value)) return value;

            String digits = value.replaceAll("[^0-9]", "");
            if (digits.length() == 13) {
                boolean hasDash = value.contains("-");
                if (hasDash) {
                    return digits.substring(0, 6) + "-*******";
                }
                return digits.substring(0, 6) + "*******";
            }

            int dashIndex = value.indexOf('-');
            if (dashIndex > 0) {
                String front = value.substring(0, dashIndex + 1);
                return front + MaskingUtil.repeat('*', value.length() - dashIndex - 1);
            }

            return MaskingUtil.maskRight(value, 6);
        }
    },

    /**
     * 이름 마스킹
     * <p>예시: 홍길동 → 홍*동</p>
     * <p>예시: 김철수 → 김*수</p>
     * <p>예시: 이영 → 이*</p>
     */
    NAME {
        @Override
        public String mask(String value) {
            if (isNullOrEmpty(value)) return value;

            if (value.length() == 1) {
                return value;
            } else if (value.length() == 2) {
                return value.charAt(0) + "*";
            } else {
                return value.charAt(0) +
                        MaskingUtil.repeat('*', value.length() - 2) +
                        value.charAt(value.length() - 1);
            }
        }
    },

    /**
     * 신용카드 번호 마스킹
     * <p>예시: 1234-5678-9012-3456 → ****-****-****-3456</p>
     * <p>예시: 1234567890123456 → ************3456</p>
     */
    CREDIT_CARD {
        @Override
        public String mask(String value) {
            if (isNullOrEmpty(value)) return value;

            String digits = value.replaceAll("[^0-9]", "");
            if (digits.length() == 16) {
                boolean hasDash = value.contains("-");
                if (hasDash) {
                    return "****-****-****-" + digits.substring(12);
                }
                return "************" + digits.substring(12);
            }

            return MaskingUtil.maskRight(value, 4);
        }
    },

    /**
     * 계좌번호 마스킹
     * <p>예시: 110-123-456789 → 110-***-***789</p>
     * <p>예시: 1234567890 → 1234***890</p>
     */
    ACCOUNT_NUMBER {
        @Override
        public String mask(String value) {
            if (isNullOrEmpty(value)) return value;

            String[] parts = value.split("-");
            if (parts.length == 3) {
                String last = parts[2];
                int visibleCount = Math.min(3, last.length());
                return parts[0] + "-***-" +
                        MaskingUtil.repeat('*', Math.max(0, last.length() - visibleCount)) +
                        (last.length() > visibleCount ? last.substring(last.length() - visibleCount) : last);
            }

            return MaskingUtil.maskMiddle(value, 4, 3);
        }
    },

    /**
     * 주소 마스킹
     * <p>예시: 서울시 강남구 테헤란로 123 → 서울시 강남구 ***</p>
     * <p>예시: 경기도 성남시 분당구 정자동 → 경기도 성남시 ***</p>
     */
    ADDRESS {
        @Override
        public String mask(String value) {
            if (isNullOrEmpty(value)) return value;

            String[] parts = value.split(" ");
            if (parts.length >= 3) {
                return parts[0] + " " + parts[1] + " ***";
            } else if (parts.length == 2) {
                return parts[0] + " ***";
            }

            return MaskingUtil.maskRight(value, 0);
        }
    },

    /**
     * 우편번호 마스킹
     * <p>예시: 12345 → 123**</p>
     * <p>예시: 06234 → 062**</p>
     */
    ZIP_CODE {
        @Override
        public String mask(String value) {
            if (isNullOrEmpty(value)) return value;

            String digits = value.replaceAll("[^0-9]", "");
            if (digits.length() == 5) {
                return digits.substring(0, 3) + "**";
            } else if (digits.length() == 6) {
                return digits.substring(0, 3) + "***";
            }

            if (value.length() <= 3) {
                return value;
            }
            return value.substring(0, 3) + MaskingUtil.repeat('*', value.length() - 3);
        }
    },

    /**
     * IP 주소 마스킹
     * <p>예시: 192.168.1.100 → 192.168.*.*</p>
     * <p>예시: 2001:0db8:85a3::8a2e:0370:7334 → 2001:0db8:****:****:****:****:****:****</p>
     */
    IP_ADDRESS {
        @Override
        public String mask(String value) {
            if (isNullOrEmpty(value)) return value;

            if (value.contains(".")) {
                String[] parts = value.split("\\.");
                if (parts.length == 4) {
                    return parts[0] + "." + parts[1] + ".*.*";
                }
            } else if (value.contains(":")) {
                String[] parts = value.split(":");
                if (parts.length >= 2) {
                    return parts[0] + ":" + parts[1] + ":****:****:****:****:****:****";
                }
            }

            return value;
        }
    },

    /**
     * 신분증(주민등록증/운전면허증) 번호 마스킹
     * <p>예시: 123456-1234567 → 123456-*******</p>
     * <p>예시: 11-12-345678-90 → 11-12-******-**</p>
     */
    ID_CARD {
        @Override
        public String mask(String value) {
            if (isNullOrEmpty(value)) return value;

            String digits = value.replaceAll("[^0-9]", "");
            if (digits.length() >= 10) {
                return MaskingUtil.maskRight(value, value.length() / 3);
            }

            return MaskingUtil.maskRight(value, 6);
        }
    },

    /**
     * 여권번호 마스킹
     * <p>예시: M12345678 → M12***678</p>
     * <p>예시: AB1234567 → AB1***567</p>
     */
    PASSPORT {
        @Override
        public String mask(String value) {
            if (isNullOrEmpty(value)) return value;

            if (value.length() <= 6) {
                return MaskingUtil.maskMiddle(value, 2, 0);
            }

            return MaskingUtil.maskMiddle(value, 3, 3);
        }
    },

    /**
     * 차량번호 마스킹
     * <p>예시: 12가3456 → 12가**56</p>
     * <p>예시: 서울12가3456 → 서울12가**56</p>
     */
    LICENSE_PLATE {
        @Override
        public String mask(String value) {
            if (isNullOrEmpty(value)) return value;

            if (value.length() <= 4) {
                return value;
            }

            return MaskingUtil.maskMiddle(value, value.length() - 4, 2);
        }
    },

    /**
     * 왼쪽 마스킹 (오른쪽 4자리 보존)
     * <p>예시: ABC12345 → ****2345</p>
     * <p>예시: 1234567890 → ******7890</p>
     */
    PARTIAL_LEFT {
        @Override
        public String mask(String value) {
            if (isNullOrEmpty(value)) return value;
            return MaskingUtil.maskLeft(value, 4);
        }
    },

    /**
     * 오른쪽 마스킹 (왼쪽 4자리 보존)
     * <p>예시: ABC12345 → ABC1****</p>
     * <p>예시: 1234567890 → 1234******</p>
     */
    PARTIAL_RIGHT {
        @Override
        public String mask(String value) {
            if (isNullOrEmpty(value)) return value;
            return MaskingUtil.maskRight(value, 4);
        }
    },

    /**
     * 중간 마스킹 (양쪽 2자리씩 보존)
     * <p>예시: ABC12345 → AB****45</p>
     * <p>예시: 1234567890 → 12******90</p>
     */
    MIDDLE {
        @Override
        public String mask(String value) {
            if (isNullOrEmpty(value)) return value;
            return MaskingUtil.maskMiddle(value, 2, 2);
        }
    };

    /**
     * 마스킹 처리 메서드
     *
     * @param value 원본 문자열
     * @return 마스킹 처리된 문자열
     */
    public abstract String mask(String value);

    private static boolean isNullOrEmpty(String value) {
        return value == null || value.isEmpty();
    }
}
