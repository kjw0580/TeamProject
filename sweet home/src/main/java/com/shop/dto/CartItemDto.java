package com.shop.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CartItemDto {
	// 장바구니에 담을 상품의 id 와 수량 전달 받음
	@NotNull(message = "상품 아이디는 필수 입력 값입니다.")
	private Long itemId;

	@Min(value = 1, message = "최소 1개 이상 담아주세요.")
	private int count;
}