package KrkrDataLoader.network.service;

import KrkrDataLoader.core.KrkrData;
import KrkrDataLoader.core.KrkrScene;
import KrkrDataLoader.core.KrkrScenes;
import KrkrDataLoader.network.KrkrResponse;
import KrkrDataLoader.network.KrkrResponseBuilder;
import KrkrDataLoader.network.KrkrResponseFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class KrkrSceneTextService
{
	/**
	 * Get range scene text.
	 *
	 * @param data  KrkrData. ( formatted scene data )
	 * @param begin Begin of range. ( included )
	 * @param end   End of range. ( not included )
	 *
	 * @return Scene texts if texts are available.
	 */
	public KrkrResponse krkrRangeText(KrkrData data, int begin, int end)
	{
		if(data == null) return KrkrResponseFactory.resourceNotReady();
		if(data.size() == 0) return new KrkrResponseBuilder().setStatus("success")
															 .setCode(206)
															 .setMessage("The information of parsed data")
															 .setData(new ArrayList<>())
															 .build();
		
		// 转换负数下标为正数
		if(begin < 0) { begin = data.size() + begin; }
		if(end < 0) { end = data.size() + end; }
		
		if(begin < 0 || begin >= end || end > data.size()) return KrkrResponseFactory.outOfRange();
		
		List<String> childrenList = new ArrayList<>();
		if(data instanceof KrkrScenes)
		{
			for(KrkrData scene: data.listChildren().subList(begin, end))
			{
				childrenList.addAll(( (KrkrScene) scene ).listDialogues());
			}
		}
		else if(data instanceof KrkrScene) childrenList = ( (KrkrScene) data ).listDialogues().subList(begin, end);
		else
			return KrkrResponseFactory.error("Bad Request: Type Error when getting Text. Promise current data is valid.");
		
		return new KrkrResponseBuilder().setStatus("success")
										.setCode(206)
										.setMessage("The information of parsed data")
										.setData(childrenList)
										.build();
	}
}
