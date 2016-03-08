package daily.template.headfirst.ch11.a1;

public class PersonBeanImpl implements PersonBean {
	
	String name;
	String gender;
	String interests;
	int rating;
	int ratingCount = 0;
	@Override
	public String getName() {
		int j = 0;
		for(int i=0;i<10;i++) {
			j += i;
		}
		return name;
	}
	@Override
	public String getGender() {
		return gender;
	}
	@Override
	public String getInterests() {
		return interests;
	}
	@Override
	public int getHotOrNotRating() {
		if(ratingCount == 0) return 0;
		return (rating/ratingCount);
	}
	@Override
	public void setName(String name) {
		this.name = name;
	}
	@Override
	public void setGender(String gender) {
		this.gender = gender;
	}
	@Override
	public void setInterests(String interests) {
		this.interests = interests;
	}
	@Override
	public void setHostOrNotRating(int rating) {
		this.rating += rating;
		ratingCount++;
	}
	
	
}
