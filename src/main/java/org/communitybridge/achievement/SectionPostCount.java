package org.communitybridge.achievement;

class SectionPostCount
{
	private String sectionID;
	private int postCount;

	public SectionPostCount(String sectionID, int postCount)
	{
		this.sectionID = sectionID;
		this.postCount = postCount;
	}
	
	@Override
	public boolean equals(Object other)
	{
		if (other == null || this.getClass() != other.getClass())
		{
			return false;
		}
		return this.equals((SectionPostCount)other);
	}

	@Override
	public int hashCode()
	{
		int hash = 3;
		hash = 79 * hash + (this.sectionID != null ? this.sectionID.hashCode() : 0);
		hash = 79 * hash + this.postCount;
		return hash;
	}
	
	public boolean equals(SectionPostCount other)
	{
		return this.sectionID.equals(other.sectionID) && this.postCount == other.postCount;
	}

	public String getSectionID()
	{
		return sectionID;
	}

	public void setSectionID(String sectionID)
	{
		this.sectionID = sectionID;
	}

	public int getPostCount()
	{
		return postCount;
	}

	public void setPostCount(int postCount)
	{
		this.postCount = postCount;
	}
}
