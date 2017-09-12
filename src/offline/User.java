
package offline;

public class User implements Comparable<User> {
	private String id;
	private Double value;

	

	public String getId() {
		return id;
	}



	public void setId(String id) {
		this.id = id;
	}

	public User(String id, Double value) {
		this.id = id;
		this.value = value;
	}


	public Double getValue() {
		return value;
	}


	public void setValue(Double value) {
		this.value = value;
	}



	@Override
	public int compareTo(User other) {
		return (int) (other.value - this.value);//other > value ? 1 : -1
	}
}
