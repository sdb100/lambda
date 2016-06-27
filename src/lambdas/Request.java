package lambdas;

public class Request {
	private String value = "default";
	
	public Request(String s){
		this.value = s;
	}

	public String getValue() {
		return value;
	}

	@Override
	public String toString(){
		return this.getValue();
	}
}
