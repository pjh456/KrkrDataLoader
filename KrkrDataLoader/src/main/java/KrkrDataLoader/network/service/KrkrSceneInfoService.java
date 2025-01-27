package KrkrDataLoader.network.service;

import KrkrDataLoader.core.KrkrData;
import KrkrDataLoader.network.KrkrResponse;
import KrkrDataLoader.network.KrkrResponseBuilder;
import KrkrDataLoader.network.KrkrResponseFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static KrkrDataLoader.network.KrkrResponseFactory.resourceNotReady;


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
				resourceNotReady() :
				new KrkrResponseBuilder().setStatus("success")
										 .setCode(200)
										 .setMessage("The information of parsed data")
										 .setData(Map.of("name", data.name, "scene_count", data.size()))
										 .build();
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
		if(data == null) return resourceNotReady();
		
		if(begin < 0) { begin = data.size() + begin; }
		if(end < 0) { end = data.size() + end; }
		
		if(begin < 0 || begin >= end || end > data.size()) return KrkrResponseFactory.outOfRange();
		
		List<Map<String,Object>> childrenList = new ArrayList<>();
		for(KrkrData child: data.listChildren().subList(begin, end))
		{
			childrenList.add(Map.of("name", child.name, "scene_count", child.size()));
		}
		
		return new KrkrResponseBuilder().setStatus("success")
										.setCode(200)
										.setMessage("The information of parsed data")
										.setData(childrenList)
										.build();
	}
}
