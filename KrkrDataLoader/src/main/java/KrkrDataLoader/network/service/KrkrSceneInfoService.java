package KrkrDataLoader.network.service;

import KrkrDataLoader.core.KrkrData;
import KrkrDataLoader.core.ParentChild;
import KrkrDataLoader.network.response.KrkrResponse;
import KrkrDataLoader.network.response.KrkrResponseBuilder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Service
public class KrkrSceneInfoService
{
	/**
	 * Get scene data info ( scene name and number of children ).
	 *
	 * @param data KrkrData. ( formatted scene data )
	 *
	 * @return Data info if data is available.
	 */
	public KrkrResponse krkrInfo(KrkrData data)
	{
		return data == null ?
			   new KrkrResponseBuilder().setStatus("accepted").setCode(202).setMessage(
					   "Resource is not ready, please try again later.").build() :
			   new KrkrResponseBuilder().setStatus("success").setCode(200).setMessage(
					   "The information of parsed data").setData(Map.of("name",
																		data.getName(),
																		"scene_size",
																		data.size()
			   )).build();
	}
	
	/**
	 * Get range data info ( scene name and number of children ).
	 *
	 * @param data  KrkrData. ( formatted scene data )
	 * @param begin Begin of range. ( included )
	 * @param end   End of range. ( not included )
	 *
	 * @return Range data info if data is available.
	 */
	public KrkrResponse krkrRangeInfo(KrkrData data, int begin, int end)
	{
		if(data == null)
		{
			return new KrkrResponseBuilder().setStatus("accepted").setCode(202).setMessage(
					"Resource is not ready, please try again later.").build();
		}
		
		// 转换负数下标为正数，注意 -1 是最后一个，所以要 +1 ！
		if(begin < 0) { begin = data.size() + begin + 1; }
		if(end < 0) { end = data.size() + end + 1; }
		
		if(begin < 0 || begin >= end || end > data.size())
		{
			return new KrkrResponseBuilder().setStatus("Failed").setCode(416).setMessage(
					"Request is out of range!").build();
		}
		
		List<Map<String,Object>> childrenList = new ArrayList<>();
		for(ParentChild child: data.listChildren().subList(begin, end))
		{
			childrenList.add(Map.of("name", child.getName(), "scene_size", child.size()));
		}
		
		return new KrkrResponseBuilder().setStatus("success").setCode(200).setMessage(
				"The information of parsed data").setData(childrenList).build();
	}
}
