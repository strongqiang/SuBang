package com.subang.service;

import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.subang.domain.TicketType;
import com.subang.exception.SuException;
import com.subang.util.SuUtil;

@Service
public class ActivityService extends BaseService {

	public void addTicketType(TicketType ticketType, MultipartFile icon) throws SuException {
		try {
			if (!icon.isEmpty()) {
				ticketType.calcIcon(icon.getOriginalFilename());
				SuUtil.saveMultipartFile(false, icon, ticketType.getIcon());
			}
			ticketTypeDao.save(ticketType);
		} catch (DuplicateKeyException e) {
			throw new SuException("优惠券名称不能相同。");
		}
	}

	public void modifyTicketType(TicketType ticketType, MultipartFile icon) throws SuException {
		try {
			if (!icon.isEmpty()) {
				ticketType.calcIcon(icon.getOriginalFilename());
				SuUtil.saveMultipartFile(true, icon, ticketType.getIcon());
			}
			ticketTypeDao.update(ticketType);
		} catch (DuplicateKeyException e) {
			throw new SuException("优惠券名称不能相同。");
		}
	}

	public void deleteTicketTypes(List<Integer> ticketTypeids) throws SuException {
		boolean isAll = true;
		for (Integer ticketTypeid : ticketTypeids) {
			try {
				SuUtil.deleteFile(ticketTypeDao.get(ticketTypeid).getIcon());
				ticketTypeDao.delete(ticketTypeid);
			} catch (DataIntegrityViolationException e) {
				isAll = false;
			}
		}
		if (!isAll) {
			throw new SuException("部分优惠券类型没有成功删除。可能是用户的优惠券引用了这些类型。");
		}
	}

	/**
	 * 数据清理
	 */
	public void clear() {
		ticketDao.deleteInvalid();
		ticketTypeDao.deleteInvalid();
	}
}
