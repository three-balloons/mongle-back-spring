package me.bubble.bubble.dto;

import lombok.Getter;

@Getter
public class ControlPoint {
    private final int x;
    private final int y;
    private final boolean visible;

    public ControlPoint() {
        // 기본값 설정
        this.x = 0;
        this.y = 0;
        this.visible = false;
    }

    public ControlPoint(ControlPoint other) {
        this.x = other.x;
        this.y = other.y;
        this.visible = other.visible;
    }

    public ControlPoint(String controlPoint) {
        // 주어진 문자열을 두 글자씩 나눔
        String xHex = controlPoint.substring(0, 2);
        String yHex = controlPoint.substring(2, 4);
        String visibleHex = controlPoint.substring(4, 6);

        // 16진수 문자열을 정수로 변환하여 x와 y에 할당
        this.x = hexToSignedByte(xHex);
        this.y = hexToSignedByte(yHex);

        // visibleHex 값을 정수로 변환하여 isVisible에 할당 (0이 아니면 true)
        int visibleInt = Integer.parseInt(visibleHex, 16);
        this.visible = visibleInt != 0;
    }

    // 16진수 문자열을 signed 8비트 정수로 변환하는 메소드
    private int hexToSignedByte(String hex) {
        int unsigned = Integer.parseInt(hex, 16);
        // 8비트 부호 있는 정수로 변환
        if (unsigned >= 0x80) {
            // 음수일 경우 2의 보수 계산
            return unsigned - 0x100;
        }
        // 양수일 경우 그대로 반환
        return unsigned;
    }


    public String toBinaryString() {
        // x와 y를 8비트 signed 이진 문자열로 변환
        String xBinary = String.format("%8s", Integer.toBinaryString(x & 0xFF)).replace(' ', '0');
        String yBinary = String.format("%8s", Integer.toBinaryString(y & 0xFF)).replace(' ', '0');

        // isVisible 값을 1비트 이진 문자열로 변환
        String isVisibleBinary = visible ? "1" : "0";
        isVisibleBinary = String.format("%8s", isVisibleBinary).replace(' ', '0'); // 8비트로 패딩

        // 이진 문자열 합치기
        String binaryString = xBinary + yBinary + isVisibleBinary;

        // 4비트씩 나누어 16진수로 변환하여 결과 문자열 생성
        StringBuilder hexString = new StringBuilder();
        for (int i = 0; i < binaryString.length(); i += 4) {
            String chunk = binaryString.substring(i, i + 4);
            int decimal = Integer.parseInt(chunk, 2);
            hexString.append(Integer.toHexString(decimal).toUpperCase());
        }

        // 결과 문자열을 6자리로 보장
        while (hexString.length() < 6) {
            hexString.insert(0, "0");
        }

        return hexString.toString();
    }

}
