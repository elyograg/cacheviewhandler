package org.elyograg.solr.handler;

import static org.apache.solr.common.params.CommonParams.FAILURE;
import static org.apache.solr.common.params.CommonParams.STATUS;

import java.util.Map;
import java.util.TreeMap;

import org.apache.lucene.search.Query;
import org.apache.solr.common.SolrException;
import org.apache.solr.core.PluginInfo;
import org.apache.solr.core.SolrCore;
import org.apache.solr.handler.RequestHandlerBase;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.response.SolrQueryResponse;
import org.apache.solr.search.DocSet;
import org.apache.solr.search.SolrCache;
import org.apache.solr.search.SolrIndexSearcher;
import org.apache.solr.util.RefCounted;
import org.apache.solr.util.plugin.PluginInfoInitialized;
import org.apache.solr.util.plugin.SolrCoreAware;

/**
 * Handler that will export keys for available built-in Solr caches and show
 * some minimally useful info about the contents of those keys, without actually
 * showing the full contents.
 */
public class CacheViewHandler extends RequestHandlerBase implements SolrCoreAware, PluginInfoInitialized {
//	private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	private SolrCore myCore;

	@Override
	public String getDescription() {
		return "Cache Viewer Handler";
	}

	@Override
	public void handleRequestBody(SolrQueryRequest req, SolrQueryResponse rsp) throws Exception {
		// TODO Auto-generated method stub
		String requestedCache = req.getParams().get("cache");
		RefCounted<SolrIndexSearcher> ref = myCore.getSearcher();
		ref.incref();
		SolrIndexSearcher searcher = ref.get();
		if (requestedCache == null) {
			rsp.add("error", "No cache requested, try cache=filter as a URL parameter.");
			rsp.add(STATUS, FAILURE);
			rsp.setException(new SolrException(SolrException.ErrorCode.BAD_REQUEST, "No cache requested"));
		} else {
			switch (requestedCache) {
			case "filter":
				populateWithFilterCacheInfo(rsp, searcher);
				break;

			default:
				rsp.add("error", "Unknown Cache requested");
				rsp.add(STATUS, FAILURE);
				rsp.setException(new SolrException(SolrException.ErrorCode.BAD_REQUEST, "Unknown Cache requested"));
				break;
			}
		}
		ref.decref();
	}

	private final void populateWithFilterCacheInfo(SolrQueryResponse rsp, SolrIndexSearcher searcher) {
		SolrCache<Query, DocSet> cache = searcher.getFilterCache();
		Map<String, Integer> map = new TreeMap<>();
		for (Query q : cache.keySet()) {
			map.put(q.toString(), cache.get(q).size());
		}
		rsp.add("filterCacheEntries", map);
	}

	@Override
	public void init(PluginInfo info) {
		super.init(info.initArgs);
	}

	@Override
	public void inform(SolrCore core) {
		myCore = core;
		// TODO See if SearchHandler has anything else useful in its inform method.
	}

}
