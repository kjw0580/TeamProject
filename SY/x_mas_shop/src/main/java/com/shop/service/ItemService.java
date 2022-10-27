package com.shop.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityNotFoundException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.shop.dto.ItemFormDto;
import com.shop.dto.ItemImgDto;
import com.shop.dto.ItemSearchDto;
import com.shop.dto.MainItemDto;
import com.shop.entity.Item;
import com.shop.entity.ItemImg;
import com.shop.repository.ItemImgRepository;
import com.shop.repository.ItemRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ItemService {
	
	private final ItemRepository itemRepo;
	private final ItemImgService itemImgService;
	private final ItemImgRepository itemImgRepo;
	
	//상품 저장(상품, 이미지)
	public Long saveItem(ItemFormDto itemFormDto, 
			List<MultipartFile> itemImgFileList) throws IOException {
		//상품 등록
		Item item = itemFormDto.createItem();
		itemRepo.save(item);
		
		//이미지 등록
		for(int i=0; i<itemImgFileList.size(); i++) {
			ItemImg itemImg = new ItemImg();
			itemImg.setItem(item); //item 객체를 참조
			
			if(i == 0) { //첫번째 이미지일 경우 대표 상품 이미지 값을 "Y"로 세팅
				itemImg.setRepimgYn("Y");
			}else {
				itemImg.setRepimgYn("N");
			}
			itemImgService.saveItemImg(itemImg, itemImgFileList.get(i));
		}
		return item.getId();
	}
	
	//상품 상세 보기(entity -> dto)
	@Transactional(readOnly = true)
	public ItemFormDto getItemDtl(Long itemId) {
		//해당 상품의 이미지 조회(id 오름차순)
		List<ItemImg> itemImgList = 
				itemImgRepo.findByItemIdOrderByIdAsc(itemId);
		List<ItemImgDto> itemImgDtoList = new ArrayList<>();
		//조회한 ItemImg 엔티티를 ItemImgDto로 만들어서 리스트에 추가함
		for(ItemImg itemImg : itemImgList) {
			ItemImgDto itemImgDto = ItemImgDto.of(itemImg);
			itemImgDtoList.add(itemImgDto);
		}
		
		//상품의 id를 통해 상품 엔티티를 조회
		//존재하지 않을 때는 EntityNotFoundException 발생
		Item item = itemRepo.findById(itemId)
				.orElseThrow(EntityNotFoundException::new);
		
		ItemFormDto itemFormDto = ItemFormDto.of(item);
		itemFormDto.setItemImgDtoList(itemImgDtoList);
		
		return itemFormDto;	
	}
	
	//상품 수정
	public Long updateItem(ItemFormDto itemFormDto, 
			List<MultipartFile> itemImgFileList) throws IOException {
		//수정할 상품 1개 조회
		Item item = itemRepo.findById(itemFormDto.getId())
				.orElseThrow(EntityNotFoundException::new);
		
		item.updateItem(itemFormDto);  //상품 수정
		
		//상품 이미지 아이디 조회
		List<Long> itemImgIds = itemFormDto.getItemImgIds();
		
		//이미지 파일 리스트를 파라미터로 전달
		for(int i=0; i<itemImgFileList.size(); i++) {
			itemImgService.updateItemImg(itemImgIds.get(i), itemImgFileList.get(i));
		}
		
		return item.getId();
	}
	
	//상품 검색 및 페이지 처리
	@Transactional(readOnly = true)
	public Page<Item> getAdminItemPage(ItemSearchDto itemSearchDto, 
			Pageable pageable){
		return itemRepo.getAdminItemPage(itemSearchDto, pageable);
	}
	
	//메인 페이지 상품 데이터 조회
	public Page<MainItemDto> getMainItemPage(ItemSearchDto itemSearchDto, Pageable pageable){
		return itemRepo.getMainItemPage(itemSearchDto, pageable);
	}
}








