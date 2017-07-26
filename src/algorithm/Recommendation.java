package algorithm;

import java.util.List;

import entity.Item;

public interface Recommendation {
  public List<Item> recommendItems(String userId, double latitude, double longitude);
}
