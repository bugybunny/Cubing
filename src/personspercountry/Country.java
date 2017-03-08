package personspercountry;

public final class Country {
    public String name;
    public int population;
    public String continent;
    public int noCubers;

    public static Country create(String name) {
        return null;
    }

    public Country(String name, String continent, int population) {
        this.name = name;
        this.continent = continent;
        this.population = population;
    }

    public Country(String name, String continent, int population, int noCubers) {
        this.name = name;
        this.continent = continent;
        this.population = population;
        this.noCubers = noCubers;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContinent() {
        return continent;
    }

    public void setContinent(String continent) {
        this.continent = continent;
    }

    public int getPopulation() {
        return population;
    }

    public void setPopulation(int population) {
        this.population = population;
    }

    public int getNoCubers() {
        return noCubers;
    }

    public void setNoCubers(int noCubers) {
        this.noCubers = noCubers;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Country other = (Country) obj;
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return name + ";" + population + ";" + continent + ";" + noCubers;
    }
}
