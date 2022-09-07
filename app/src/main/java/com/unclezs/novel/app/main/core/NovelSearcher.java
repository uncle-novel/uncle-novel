package com.unclezs.novel.app.main.core;

import cn.hutool.core.collection.ListUtil;
import com.unclezs.novel.analyzer.common.concurrent.ThreadUtils;
import com.unclezs.novel.analyzer.common.page.AbstractPageable;
import com.unclezs.novel.analyzer.core.model.AnalyzerRule;
import com.unclezs.novel.analyzer.model.Novel;
import com.unclezs.novel.analyzer.spider.SearchSpider;
import com.unclezs.novel.app.framework.components.Toast;
import com.unclezs.novel.app.framework.executor.Executor;
import com.unclezs.novel.app.main.enums.SearchType;
import com.unclezs.novel.app.main.manager.RuleManager;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author blog.unclezs.com
 * @since 2021/5/28 22:05
 */
@Slf4j
public class NovelSearcher {

  private static ExecutorService service;
  private final List<SearchSpider> searchers = new ArrayList<>();
  private final AtomicInteger counter = new AtomicInteger();
  private final boolean audio;
  private final Callback callback;
  private String keyword;
  @Getter
  private boolean hasMore;
  @Getter
  private boolean searching;

  public NovelSearcher(boolean audio, Callback callback) {
    this.audio = audio;
    this.callback = callback;
  }

  public synchronized void doSearch(String keyword, String type) {
    if (service == null) {
      service = ThreadUtils.newFixedThreadPoolExecutor(10, "search");
    }
    this.keyword = keyword;
    cancel();
    List<AnalyzerRule> rules = audio ? RuleManager.audioSearchRules() : RuleManager.textSearchRules();
    if (rules.isEmpty()) {
      Toast.warn("未发现可用搜索书源");
      callback.showLoading(false);
      return;
    }
    counter.set(rules.size());
    hasMore = true;
    searching = true;
    callback.showLoading(true);
    for (AnalyzerRule rule : rules) {
      SearchSpider searcher = new SearchSpider(ListUtil.of(rule));
      searcher.setOnNewItemAddHandler(novel -> {
        if (SearchType.match(type, keyword, novel)) {
          Executor.runFx(() -> callback.addItem(novel));
        }
      });
      searchers.add(searcher);
      searchOrLoadMore(searcher, false);
    }
  }

  public void cancel() {
    if (!searchers.isEmpty()) {
      searchers.forEach(AbstractPageable::cancel);
    }
    searchers.clear();
    callback.showLoading(false);
    this.hasMore = false;
  }

  public synchronized void loadMore() {
    if (checkHasMore() && !searching) {
      searching = true;
      callback.showLoading(true);
      counter.set(searchers.size());
      searchers.forEach(searchSpider -> searchOrLoadMore(searchSpider, true));
    }
  }

  public synchronized void searchOrLoadMore(SearchSpider searcher, boolean more) {
    service.execute(() -> {
      try {
        if (more) {
          searcher.loadMore();
        } else {
          searcher.search(keyword);
        }
      } catch (Exception e) {
        log.error("搜索失败：{}", keyword, e);
      } finally {
        if (counter.decrementAndGet() == 0) {
          checkHasMore();
          searching = false;
          Executor.runFx(() -> {
            callback.showLoading(false);
            if (!hasMore) {
              callback.noMore();
            }
          });
        }
        // 如果没有更多了，直接移除
        if (!searcher.hasMore()) {
          searchers.remove(searcher);
        }
      }
    });
  }

  private boolean checkHasMore() {
    hasMore = searchers.stream().anyMatch(AbstractPageable::hasMore);
    return hasMore;
  }

  public interface Callback {

    void showLoading(boolean loading);

    void addItem(Novel novel);

    default void noMore() {
      Toast.info("没有更多了");
    }
  }
}
