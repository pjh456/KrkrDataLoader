package KrkrDataLoader.network;

public interface RangeCallback
{
	public KrkrResponse callback(Object data, int begin, int end);
	public default KrkrResponse callback(Object data, int begin){return callback(data,0, -1);}
	public default KrkrResponse callback(Object data){return callback(data, 0);}
}
