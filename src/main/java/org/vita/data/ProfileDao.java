package org.vita.data;

import org.vita.models.Profile;

public interface ProfileDao
{
    Profile create(Profile profile);
    Profile getByUserId(int id);
    Profile update(Profile profile);
}
