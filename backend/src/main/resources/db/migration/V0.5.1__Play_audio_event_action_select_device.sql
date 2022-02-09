alter table event_actions
    add column audio_device text;

update event_actions
set audio_device = ''
where dtype = 'PlayAudio';

alter table event_actions
    add constraint event_actions_audio_device_check check ((dType = 'PlayAudio' AND audio_device IS NOT NULL) OR audio_device IS NULL);