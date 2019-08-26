package com.northmeter.northmetersmart.model;
/**节能天数和排名的model*/
public class Energy_And_Rank_Model {

	private String text_name;//表号，名字
	private String text_energy;//用电天数
	private String text_energy_save;//节能天数
	private String text_percent;//用电比例
	private String text_rank;//排名
	private String text_beat;//打败多少对手
	private int background;//背景颜色
	public String getText_name() {
		return text_name;
	}
	public void setText_name(String text_name) {
		this.text_name = text_name;
	}
	public String getText_energy() {
		return text_energy;
	}
	public void setText_energy(String text_energy) {
		this.text_energy = text_energy;
	}
	public String getText_rank() {
		return text_rank;
	}
	public void setText_rank(String text_rank) {
		this.text_rank = text_rank;
	}
	public String getText_energy_save() {
		return text_energy_save;
	}
	public void setText_energy_save(String text_energy_save) {
		this.text_energy_save = text_energy_save;
	}
	public String getText_percent() {
		return text_percent;
	}
	public void setText_percent(String text_percent) {
		this.text_percent = text_percent;
	}
	
	
	public int getBackground() {
		return background;
	}
	public void setBackground(int background) {
		this.background = background;
	}
	public String getText_beat() {
		return text_beat;
	}
	public void setText_beat(String text_beat) {
		this.text_beat = text_beat;
	}
	private Energy_And_Rank_Model(String name,String energy,String energy_save,String percent,String rank,String beat,int background){
		this.text_name = name;
		this.text_energy = energy;
		this.text_energy_save = energy_save;
		this.text_percent = percent;
		this.text_rank = rank;
		this.text_beat = beat;
		this.background = background;
	}
	public Energy_And_Rank_Model(){
		
	}
}
